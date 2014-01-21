package eu.liveandgov.wp1.sensor_collector;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.activity_recognition.HarAdapter;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.ConnectorThread;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.GpsCache;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.LinkedSensorQueue;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.monitor.MonitorThread;
import eu.liveandgov.wp1.sensor_collector.persistence.FilePersistor;
import eu.liveandgov.wp1.sensor_collector.persistence.Persistor;
import eu.liveandgov.wp1.sensor_collector.persistence.PublicationPipeline;
import eu.liveandgov.wp1.sensor_collector.persistence.ZipFilePersistor;
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

    // SENSOR CONSUMERS
    public Persistor persistor;
    public PublicationPipeline publisher;
    public Consumer<String> streamer;
    public Consumer<String> harPipeline;
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

        // Init sensor consumers
        streamer = new ZmqStreamer();
        harPipeline = new HarAdapter();
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


        /* The ServiceSensorControl stores temporarily the current route of the users to be visualized on the map.*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(ExtendedIntentAPI.RETURN_GPS_SAMPLE);
        registerReceiver(receiver, filter);
        counter = 0l;
        currentRoute = new ArrayList<LatLng>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        persistor.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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

        listener.onDeletionCompleted();
    }

    private void doStopHAR() {
        isHAR = false;
        connectorThread.removeConsumer(harPipeline);
    }

    private void doStartHAR() {
        isHAR = true;
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

        //Show notification when the transfering is in progress.
        onStartTransfering();
        //CHANGED END
        transferManager.doTransfer();
    }

    private void doDisableRecording() {
        connectorThread.removeConsumer(persistor);
        isRecording = false;

        //Reset the current route of the user.
        currentRoute.clear();
        counter = 0l;

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

        //Reset the current route of the user if it has not already been reset.
        currentRoute.clear();
        counter = 0l;

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

    //Id for the "in-progress" transfering notification
    private static final int transferringNotificationID = 3;
    //Coordinates list of the current route of the user
    private ArrayList<LatLng> currentRoute;
    private long counter;
    //Access to the service by an activity
    private final IBinder mBinder = new LocalBinder();
    //Callbacks for transfering status (success/failure)
    private SensorServiceListener listener = SensorServiceListener.NULL_LISTENER;
    //Used to receive the gps entries for the current route of the user
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            onGpsSampleReceived(intent.getStringExtra(ExtendedIntentAPI.FIELD_GPS_ENTRY));
        }

    };
    //Access to the Service.
    public class LocalBinder extends Binder {
        public ServiceSensorControl getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServiceSensorControl.this;
        }
    }
    //Callback for deleting samples
    public interface SensorServiceListener
    {
        public static final SensorServiceListener NULL_LISTENER = new SensorServiceListener() {
            @Override
            public void onDeletionCompleted() {

            }
        };

        public void onDeletionCompleted();
    }
    //Callback for transfer
    public interface TransferListener
    {
        public static final TransferListener NULL_LISTENER = new TransferListener() {
            @Override
            public void onTransferCompleted(boolean success) {

            }
        };

        public void onTransferCompleted(boolean success);
    }
    //When gpsData is received (for current route of the user)
    private void onGpsSampleReceived (String gpsSample)
    {
        //Dont visualize all gpsSamples on the map (memory issues). Skip 4 out of 5
        if(counter%5 == 0)
        {
            String[] gpsData = gpsSample.split(",")[3].split("\\s+");
            currentRoute.add(new LatLng(Double.parseDouble(gpsData[0]), Double.parseDouble(gpsData[1])));
        }
        counter++;
    }
    //Get the user's current route
    public ArrayList<LatLng> getCurrentRoute()
    {
        return currentRoute;
    }

    //Called when the transfering has started - Show notification to the user
    private void onStartTransfering()
    {
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Recordings")
                .setContentText("Uploading samples")
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setProgress(0, 0, true);
        mNotifyManager.notify(transferringNotificationID, mBuilder.build());
        ((TransferThreadPost) transferManager).listener = new TransferListener() {

            @Override
            public void onTransferCompleted(boolean success) {
                if(success)
                {
                    mBuilder.setContentText("Upload completed successfully").setProgress(0, 0, false);
                    mNotifyManager.notify(transferringNotificationID, mBuilder.build());
                    transferManager.deleteStagedSamples();
                    persistor.deleteSamples();
                    publisher.deleteSamples();
                    if(listener != null)
                        listener.onDeletionCompleted();
                }
                else
                {
                    mBuilder.setContentText("Upload failed").setProgress(0, 0, false);
                    mNotifyManager.notify(transferringNotificationID, mBuilder.build());
//					if(listener!= null)
//						listener.onDeletionCompleted();
                }
            }
        };
    }
    //Register the listener by an activity
    public void setOnSamplesDeletedListener(SensorServiceListener l)
    {
        listener = l;
    }
    public boolean samplesStored()
    {
        //Possible bug : persistor.hasSamples() returns true always
        return persistor.hasSamples() || transferManager.hasStagedSamples();
    }
}
