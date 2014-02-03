package eu.liveandgov.wp1.sensor_collector;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.activity_recognition.HarAdapter;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.PPSOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.ConnectorThread;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.GpsCache;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.LinkedSensorQueue;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.monitor.MonitorThread;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.sensor_collector.persistence.FilePersistor;
import eu.liveandgov.wp1.sensor_collector.persistence.Persistor;
import eu.liveandgov.wp1.sensor_collector.persistence.PublicationPipeline;
import eu.liveandgov.wp1.sensor_collector.persistence.ZipFilePersistor;
import eu.liveandgov.wp1.sensor_collector.pps.PPSAdapter;
import eu.liveandgov.wp1.sensor_collector.pps.api.AggregatingPS;
import eu.liveandgov.wp1.sensor_collector.pps.api.csv.StaticIPS;
import eu.liveandgov.wp1.sensor_collector.pps.api.ooapi.OSMIPPS;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorThread;
import eu.liveandgov.wp1.sensor_collector.streaming.ZmqStreamer;
import eu.liveandgov.wp1.sensor_collector.transfer.TransferManager;
import eu.liveandgov.wp1.sensor_collector.transfer.TransferThreadPost;

import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.API_EXTENSIONS;
import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.ZIPPED_PERSISTOR;

public class ServiceSensorControl extends Service {
    // CONSTANTS
    static final String LOG_TAG =  "SCS";
    public static final String SENSOR_FILENAME = "sensor.ssf";
    public static final String STAGE_FILENAME = "sensor.stage.ssf";

    private static final String SHARED_PREFS_NAME = "SensorCollectorPrefs";
    private static final String PREF_ID = "userid";

    // STATUS FLAGS
    public boolean isRecording = false;
    public boolean isStreaming = false;
    public boolean isHAR = false;
    public String userId = ""; // will be set onCreate

    // COMMUNICATION CHANNEL
    public SensorQueue sensorQueue = new LinkedSensorQueue();

    // REMARK:
    // Need to put initialization to onCreate, since FilesDir, etc. is not available
    // from a static context.

    // INDICES
    public StaticIPS staticIPS;
    public OSMIPPS osmIPPS;
    public AggregatingPS aggregatorPS;

    // SENSOR CONSUMERS
    public Persistor persistor;
    public PublicationPipeline publisher;
    public Consumer<String> streamer;
    public Consumer<String> harPipeline;
    public Consumer<String> ppsPipeline;
    public GpsCache gpsCache;

    // THREADS
    public ConnectorThread connectorThread;
    public TransferManager transferManager;
    public MonitorThread monitorThread;
    // Rem: Also SensorThread would belong here, but it is realized via static methods

    /* CONSTRUCTOR */
    public ServiceSensorControl() {
        // Register this object globally
        GlobalContext.set(this);
    }

    /* ANDROID LIFECYCLE */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "Creating ServiceSensorControl");

        // INITIALIZATIONS
        // Warning: getFilesDir is only available after onCreate was called.
        File sensorFile   = new File(getFilesDir(), SENSOR_FILENAME);
        File stageFile    = new File(getFilesDir(), STAGE_FILENAME);

        // Init indices
        staticIPS = new StaticIPS(
                PPSOptions.INDEX_HORIZONTAL_RESOLUTION,
                PPSOptions.INDEX_VERTICAL_RESOLUTION,
                PPSOptions.INDEX_BY_CENTROID,
                PPSOptions.INDEX_STORE_DEGREE,
                this,
                PPSOptions.HELSINKIIPPS_ASSET,
                false,
                PPSOptions.HELSINKI_ID_FIELD,
                PPSOptions.HELSINKI_LAT_FIELD,
                PPSOptions.HELSINKI_LON_FIELD,
                PPSOptions.PROXIMITY);

        osmIPPS = new OSMIPPS(
                PPSOptions.INDEX_HORIZONTAL_RESOLUTION,
                PPSOptions.INDEX_VERTICAL_RESOLUTION,
                PPSOptions.INDEX_BY_CENTROID,
                PPSOptions.INDEX_STORE_DEGREE,
                PPSOptions.OSMIPPS_BASE_URL,
                PPSOptions.PROXIMITY);

        aggregatorPS = new AggregatingPS();
        aggregatorPS.getProximityServices().add(staticIPS);
        aggregatorPS.getProximityServices().add(osmIPPS);

        // Init sensor consumers
        streamer = new ZmqStreamer();
        harPipeline = new HarAdapter();
        ppsPipeline = new PPSAdapter("platform", aggregatorPS);
        gpsCache    = new GpsCache();
        persistor   = ZIPPED_PERSISTOR ?
                new ZipFilePersistor(sensorFile):
                new FilePersistor(sensorFile);
        publisher = new PublicationPipeline(); // for external communication

        // INIT THREADS
        connectorThread = new ConnectorThread(sensorQueue);
        transferManager = new TransferThreadPost(persistor, stageFile);
        monitorThread   = new MonitorThread();

        // Restore user id from shared preferences
        restoreUserId();

        // Setup sensor thread
        SensorThread.setup(sensorQueue);

        // Start Recording once the first consumers connects to connector thread.
        // This should be done once the SensorThread is already running.
        connectorThread.registerNonEmptyCallback(new ConnectorThread.Callback() {
            public void call() {
                SensorThread.startAllRecording();
            }
        });
        connectorThread.registerEmptyCallback(new ConnectorThread.Callback() {
            public void call() {
                SensorThread.stopAllRecording();
            }
        });

        // Setup monitoring thread
        monitorThread.registerMonitorable(connectorThread, "SampleCount");
        monitorThread.registerMonitorable(persistor, "Persitor");
        monitorThread.registerMonitorable(transferManager, "Transfer");
        monitorThread.registerMonitorable(sensorQueue, "Queue");

        // Start threads
        connectorThread.start();
        monitorThread.start();
        SensorThread.start();
    }

    @Override
    public void onDestroy() {
        persistor.close();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* INTENT API */

    /**
     * Dispatches incoming intents.
     * See {@link eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI} for valid intents.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.i(LOG_TAG, "No intent received.");
            return START_STICKY;
        }

        String action = intent.getAction();
        Log.v(LOG_TAG, "Received intent with action " + action);

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
        } else if (action.equals(ExtendedIntentAPI.ACTION_DELETE_SAMPLES)) {
            doDeleteSamples();
        } else {
            Log.i(LOG_TAG, "Received unknown action " + action);
        }

        return START_STICKY;
    }

    private void doDeleteSamples() {
        persistor.deleteSamples();
        publisher.deleteSamples();
        transferManager.deleteStagedSamples();

        // Also delete index files
        new File(getFilesDir(), PPSOptions.HELSINKIIPPS_INDEX_FILE).delete();
        new File(getFilesDir(), PPSOptions.OSMIPPS_INDEX_FILE).delete();
    }

    private void doStopHAR() {
        connectorThread.removeConsumer(harPipeline);
        connectorThread.removeConsumer(ppsPipeline);

        isHAR = false;

        // On har-stop, save indices
        staticIPS.trySave(new File(getFilesDir(), PPSOptions.HELSINKIIPPS_INDEX_FILE));
        osmIPPS.trySave(new File(getFilesDir(), PPSOptions.OSMIPPS_INDEX_FILE));

    }

    private void doStartHAR() {
        // On har-start, load indices
        staticIPS.tryLoad(new File(getFilesDir(), PPSOptions.HELSINKIIPPS_INDEX_FILE));
        osmIPPS.tryLoad(new File(getFilesDir(), PPSOptions.OSMIPPS_INDEX_FILE));

        isHAR = true;
        connectorThread.addConsumer(ppsPipeline);
        connectorThread.addConsumer(harPipeline);
    }

    private void doStopStreaming() {
        isStreaming = false;
        connectorThread.removeConsumer(streamer);
    }

    private void doStartStreaming() {
        isStreaming = true;
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
        Log.d("AN", "Adding annotation:" + tag);

        sensorQueue.push(SensorSerializer.tag.toSSFDefault(tag));
    }

    private void doTransferSamples() {
        transferManager.doTransfer();
    }

    private void doDisableRecording() {
        connectorThread.removeConsumer(persistor);
        isRecording = false;

        persistor.push(SensorSerializer.tag.toSSFDefault(IntentAPI.VALUE_STOP_RECORDING));

        // API EXTENSIONS are triggered on together with recording
        if (API_EXTENSIONS) {
            // Add "STOP RECORDING TAG" to publisher
            publisher.push(SensorSerializer.tag.toSSFDefault(IntentAPI.VALUE_STOP_RECORDING));
            connectorThread.removeConsumer(publisher);
            connectorThread.removeConsumer(gpsCache);
        }
    }

    private void doEnableRecording() {
        connectorThread.addConsumer(persistor);
        isRecording = true;

        persistor.push(SensorSerializer.tag.toSSFDefault(IntentAPI.VALUE_START_RECORDING));

        // API EXTENSIONS are triggered on together with recording
        if (API_EXTENSIONS) {
            publisher.push(SensorSerializer.tag.toSSFDefault(IntentAPI.VALUE_START_RECORDING));
            connectorThread.addConsumer(publisher);
            connectorThread.addConsumer(gpsCache);
        }
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

        Intent intent = new Intent(ExtendedIntentAPI.RETURN_GPS_SAMPLES);
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
