package eu.liveandgov.sensorcollectorv3;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

import eu.liveandgov.sensorcollectorv3.configuration.ExtendedIntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.GpsCache;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.ConnectorThread;
import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.human_activity_recognition.HarPipeline;
import eu.liveandgov.sensorcollectorv3.monitor.MonitorThread;
import eu.liveandgov.sensorcollectorv3.persistence.FilePersistor;
import eu.liveandgov.sensorcollectorv3.persistence.Persistor;
import eu.liveandgov.sensorcollectorv3.persistence.PublicationPipeline;
import eu.liveandgov.sensorcollectorv3.streaming.ZmqStreamer;
import eu.liveandgov.sensorcollectorv3.connectors.sensor_queue.LinkedSensorQueue;
import eu.liveandgov.sensorcollectorv3.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.sensors.SensorSerializer;
import eu.liveandgov.sensorcollectorv3.sensors.SensorThread;
import eu.liveandgov.sensorcollectorv3.transfer.TransferManager;
import eu.liveandgov.sensorcollectorv3.transfer.TransferThreadPost;

public class ServiceSensorControl extends Service {
    // CONSTANTS
    static final String LOG_TAG =  "SCS";
    public static final String SENSOR_FILENAME = "sensor.ssf";
    public static final String STAGE_FILENAME = "sensor.stage.ssf";

    private static final String SHARED_PREFS_NAME = "SensorCollectorPrefs";
    private static final String PREF_ID = "userid";

    // REMARK:
    // Need to put initialization to onCreate, since FilesDir, etc. is not available
    // from a static context.

    // STATUS FLAGS
    public boolean isRecording = false;
    public boolean isStreaming = false;
    public boolean isHAR = false;
    public String userId = "";

    // COMMUNICATION CHANNELS
    public SensorQueue sensorQueue;
    public Persistor persistor;
    public Consumer<String> publisher;
    public Consumer<String> streamer;
    public Consumer<String> harPipeline;
    public GpsCache gpsCache;

    // THREADS
    public ConnectorThread connectorThread;
    public TransferManager transferManager;
    public MonitorThread monitorThread;
    // Rem: Also SensorThread belongs here, but it is realized via static methods

    /* CONSTRUCTOR */
    public ServiceSensorControl() {}

    /* ANDROID LIFECYCLE */
    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "Creating ServiceSensorControl }");

        // Setup static variables
        GlobalContext.set(this);

        // Set Default UserID to AndroidID
        restoreUserId();

        // INITIALIZATIONS
        final File sensorFile   = new File(getFilesDir(), SENSOR_FILENAME);
        final File stageFile    = new File(getFilesDir(), STAGE_FILENAME);

        // INIT COMMUNICATION CHANNELS
        sensorQueue = new LinkedSensorQueue();
        persistor   = new FilePersistor(sensorFile);
        streamer    = new ZmqStreamer();
        harPipeline = new HarPipeline();
        gpsCache    = new GpsCache();

        // EXTERNAL COMMUNICATION
        publisher = new PublicationPipeline();

        // INIT THREADS
        connectorThread = new ConnectorThread(sensorQueue);
        transferManager = new TransferThreadPost(persistor, stageFile);
        monitorThread   = new MonitorThread();

        // Setup sensor thread
        SensorThread.setup(sensorQueue);

        // Connect sensorQueue to Consumers
        connectorThread.addConsumer(persistor);
        connectorThread.addConsumer(publisher);
        connectorThread.addConsumer(gpsCache);
        // streamer and harPipeline are added on demand in the methods below

        // Setup monitoring thread
        monitorThread.registerMonitorable(connectorThread, "SampleCount");
        monitorThread.registerMonitorable(persistor, "Persitor");
        monitorThread.registerMonitorable(transferManager, "Transfer");
        monitorThread.registerMonitorable(sensorQueue, "Queue");

        // Start threads
        SensorThread.start();
        connectorThread.start();
        monitorThread.start();

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* INTENT API */

    /**
     * Dispatches incoming intents
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.i(LOG_TAG, "No intent received.");
            return START_STICKY;
        }

        String action = intent.getAction();
        Log.d(LOG_TAG, "Received intent with action " + action);

        if (action == null) return START_STICKY;

        // Dispatch IntentAPI
        if (action.equals(IntentAPI.ACTION_RECORDING_ENABLE)) {
            doEnableRecording();
            doSendStatus();
        } else if (action.equals(IntentAPI.RECORDING_DISABLE)) {
            doDisableRecording();
            doSendStatus();
        } else if (action.equals(IntentAPI.ACTION_TRANSFER_SAMPLES)) {
            doTransferSamples();
            doSendStatus();
        } else if (action.equals(IntentAPI.ACTION_ANNOTATE)) {
            doAnnotate(intent.getStringExtra(IntentAPI.FIELD_ANNOTATION));

            doSendGps(); // ONLY TESTING REMOVE THIS!!!
        } else if (action.equals(IntentAPI.ACTION_GET_STATUS)) {
            doSendStatus();
        } else if (action.equals(IntentAPI.ACTION_START_HAR)) {
            doStartHAR();
        } else if (action.equals(IntentAPI.ACTION_STOP_HAR)) {
            doStopHAR();
        } else if (action.equals(ExtendedIntentAPI.START_STREAMING)) {
            doStartStreaming();
        } else if (action.equals(ExtendedIntentAPI.STOP_STREAMING)) {
            doStopStreaming();
        } else if (action.equals(IntentAPI.ACTION_SET_ID)) {
            doSetId(intent.getStringExtra(IntentAPI.FIELD_USER_ID));
        } else if (action.equals(ExtendedIntentAPI.ACTION_GET_GPS)) {
            doSendGps();
        } else {
            Log.i(LOG_TAG, "Received unknown action " + action);
        }

        return START_STICKY;
    }

    private void doStopHAR() {
        isHAR = false;
        connectorThread.removeConsumer(harPipeline);
    }

    private void doStartHAR() {
        isHAR = true;
        // make sure we do not add the consumer twice
        connectorThread.removeConsumer(harPipeline);
        connectorThread.addConsumer(harPipeline);
    }

    private void doStopStreaming() {
        isStreaming = false;
        connectorThread.removeConsumer(streamer);
    }

    private void doStartStreaming() {
        isStreaming = true;
        // make sure we do not add the consumer twice
        connectorThread.removeConsumer(streamer);
        connectorThread.addConsumer(streamer);
    }

    private void doSetId(String id) {
        Log.i(LOG_TAG, "Set id to:" + id);

        // Update Shared Preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, 0);
        if (settings == null) throw new IllegalStateException("Failed to load SharedPreferences");
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ID, id);
        editor.commit();

        userId = id;

        doAnnotate("USER_ID SET TO: " + id);
    }

    private void doAnnotate(String tag) {
        Log.i(LOG_TAG, "Adding annotation:" + tag);
        String msg = SensorSerializer.fromTag(tag);
        sensorQueue.push(msg);
    }

    private void doTransferSamples() {
        transferManager.doTransfer();
    }

    private void doDisableRecording() {
        doAnnotate(IntentAPI.VALUE_STOP_RECORDING);
        SensorThread.stopAllRecording();
        isRecording = false;
    }

    private void doEnableRecording() {
        doAnnotate(IntentAPI.VALUE_START_RECORDING);
        SensorThread.startAllRecording();
        isRecording = true;
    }

    public void doSendStatus() {
        Intent intent = new Intent(IntentAPI.RETURN_STATUS);
        intent.putExtra(IntentAPI.FIELD_SAMPLING, isRecording);
        intent.putExtra(IntentAPI.FIELD_TRANSFERRING,
                transferManager.isTransferring());
        intent.putExtra(IntentAPI.FIELD_SAMPLES_STORED,
                persistor.hasSamples() | transferManager.hasStagedSamples()
        );
        intent.putExtra(ExtendedIntentAPI.FIELD_STREAMING, isStreaming);
        intent.putExtra(IntentAPI.FIELD_HAR, isHAR);
        intent.putExtra(IntentAPI.FIELD_USER_ID, userId);
        sendBroadcast(intent);
    }

    private void doSendGps() {
        if (gpsCache == null) Log.w(LOG_TAG, "gpsCache not initialized!");

        Intent intent = new Intent(ExtendedIntentAPI.RETURN_GPS);
        intent.putExtra(ExtendedIntentAPI.FIELD_GPS_ENTRIES, gpsCache.getEntryString());

        sendBroadcast(intent);

        Log.i(LOG_TAG, "Sent gps message " + gpsCache.getEntryString());
    }

    // HELPER METHODS

    /**
     * Restore UserId from SharedPreferences.
     * Uses AndoridID if no Id is found.
     */
    private void restoreUserId() {
        String androidId = Settings.Secure.getString(GlobalContext.context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, 0);
        if (settings == null) throw new IllegalStateException("Failed to load SharedPreferences");
        userId = settings.getString(PREF_ID, androidId); // use androidId as default;
    }


}
