package eu.liveandgov.wp1.sensor_collector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.google.common.base.Function;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pps.api.csv.StaticIPS;
import eu.liveandgov.wp1.sensor_collector.activity_recognition.HARAdapter;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.PPSOptions;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.configuration.WaitingOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.ConnectorThread;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.GpsCache;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.LinkedSensorQueue;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.monitor.MonitorThread;
import eu.liveandgov.wp1.sensor_collector.persistence.FilePersistor;
import eu.liveandgov.wp1.sensor_collector.persistence.Persistor;
import eu.liveandgov.wp1.sensor_collector.persistence.PublicationPipeline;
import eu.liveandgov.wp1.sensor_collector.persistence.ZipFilePersistor;
import eu.liveandgov.wp1.sensor_collector.pps.PPSAdapter;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorThread;
import eu.liveandgov.wp1.sensor_collector.streaming.ZMQStreamer;
import eu.liveandgov.wp1.sensor_collector.transfer.IntentTransfer;
import eu.liveandgov.wp1.sensor_collector.transfer.TransferManager;
import eu.liveandgov.wp1.sensor_collector.transfer.TransferThreadPost;
import eu.liveandgov.wp1.sensor_collector.waiting.WaitingAdapter;

import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.API_EXTENSIONS;
import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.INTENT_TRANSFER;
import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.JSON_PERSISTOR;
import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.MAIN_EXECUTOR_CORE_TIMEOUT;
import static eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions.ZIPPED_PERSISTOR;

public class ServiceSensorControl extends Service {
    private final Logger log = LogPrincipal.get();

    // CONSTANTS
    public static final String SENSOR_FILENAME = "sensor.ssf";
    public static final String STAGE_FILENAME = "sensor.stage.ssf";

    private static final String SHARED_PREFS_NAME = "SensorCollectorPreferences";
    private static final String PREF_ID = "userid";
    private static final String PREF_SECRET = "user_secret";


    // MAIN EXECUTION SERVICE
    public final ScheduledThreadPoolExecutor executorService;

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

    // SENSOR CONSUMERS
    public Persistor persistor;
    public PublicationPipeline publisher;
    public Consumer<Item> streamer;
    public Consumer<Item> harPipeline;
    public Consumer<Item> ppsPipeline;
    public Consumer<Item> waitingPipeline;
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

        // Create the executor service, keep two threads in the pool
        executorService = new ScheduledThreadPoolExecutor(SensorCollectionOptions.MAIN_EXECUTOR_CORE_POOL);

        // If feature is available, enable core thread timeout with five seconds
        if (Build.VERSION.SDK_INT >= 9) {
            executorService.setKeepAliveTime(MAIN_EXECUTOR_CORE_TIMEOUT, TimeUnit.MILLISECONDS);
            executorService.allowCoreThreadTimeOut(true);
        }
    }


    /* ANDROID LIFECYCLE */
    @Override
    public void onCreate() {
        super.onCreate();
        LogPrincipal.configure();
        log.info("Creating ServiceSensorControl");

        // INITIALIZATIONS
        // Warning: getFilesDir is only available after onCreate was called.
        File sensorFile = new File(GlobalContext.getFileRoot(), SENSOR_FILENAME);
        File stageFile = new File(GlobalContext.getFileRoot(), STAGE_FILENAME);

        // Init index
        staticIPS = new StaticIPS(
                PPSOptions.INDEX_HORIZONTAL_RESOLUTION,
                PPSOptions.INDEX_VERTICAL_RESOLUTION,
                PPSOptions.INDEX_BY_CENTROID,
                PPSOptions.INDEX_STORE_DEGREE,
                new Callable<InputStream>() {
                    @Override
                    public InputStream call() throws IOException {
                        return getAssets().open(PPSOptions.HELSINKIIPPS_ASSET);
                    }
                },
                false,
                PPSOptions.HELSINKI_ID_FIELD,
                PPSOptions.HELSINKI_LAT_FIELD,
                PPSOptions.HELSINKI_LON_FIELD,
                PPSOptions.PROXIMITY
        );

        // Init sensor consumers
        final ZMQStreamer zmqStreamer = new ZMQStreamer();
        streamer = zmqStreamer.itemNode;

        harPipeline = new HARAdapter();
        ppsPipeline = new PPSAdapter("platform", staticIPS);
        waitingPipeline = new WaitingAdapter("platform", WaitingOptions.WAITING_TRESHOLD);
        gpsCache = new GpsCache();
        publisher = new PublicationPipeline(); // for external communication

        // Serialization used for persisting items
        Function<Item, String> persistorSerialization = JSON_PERSISTOR ?
                Persistor.JSON_SERIALIZATION :
                Persistor.REGULAR_SERIALIZATION;

        // Persistor taking and storing the items
        persistor = ZIPPED_PERSISTOR ?
                new ZipFilePersistor(sensorFile, persistorSerialization) :
                new FilePersistor(sensorFile, persistorSerialization);

        // INIT THREADS
        connectorThread = new ConnectorThread(sensorQueue);
        transferManager = INTENT_TRANSFER ?
                new IntentTransfer(persistor, GlobalContext.getFileRoot()) :
                new TransferThreadPost(persistor, stageFile);
        monitorThread = new MonitorThread();

        // Restore user id from shared preferences
        restoreUserId();

        // Setup sensor thread
        SensorThread.setup(sensorQueue);

        final int recordingNotificationId = 1;

        // Start Recording once the first consumers connects to connector thread.
        // This should be done once the SensorThread is already running.
        connectorThread.nonEmpty.register(new Callback<Consumer<? super Item>>() {
            @Override
            public void call(Consumer<? super Item> consumer) {
                log.debug("Start recording sensors, configuration " + SensorCollectionOptions.con());

                // Notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Notification notification = new NotificationCompat.Builder(ServiceSensorControl.this)
                        .setContentTitle("Sensor miner")
                        .setContentText("Recording sensor data")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLights(0xff0000ff, 900, 900)
                        .setOngoing(true)
                        .setProgress(0, 0, true)
                        .build();

                notificationManager.notify(recordingNotificationId, notification);

                SensorThread.startAllRecording();
            }
        });
        connectorThread.empty.register(new Callback<Consumer<? super Item>>() {
            @Override
            public void call(Consumer<? super Item> consumer) {
                log.debug("Stop recording sensors");

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(recordingNotificationId);

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
        log.debug("Called onDestroy()");

        persistor.close();

        executorService.shutdown();

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
            log.debug("No intent received.");
            return START_STICKY;
        }

        String action = intent.getAction();
        log.debug("Received intent with action " + action);

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
            log.debug("Received unknown action " + action);
        }

        return START_STICKY;
    }

    private void doDeleteSamples() {
        persistor.deleteSamples();
        publisher.deleteSamples();
        transferManager.deleteStagedSamples();
    }

    private void doStopHAR() {
        connectorThread.removeConsumer(harPipeline);
        connectorThread.removeConsumer(ppsPipeline);
        connectorThread.removeConsumer(waitingPipeline);

        isHAR = false;

        // On har-stop, save indices
        staticIPS.trySave(new File(getFilesDir(), PPSOptions.HELSINKIIPPS_INDEX_FILE));

    }

    private void doStartHAR() {
        // On har-start, load indices
        staticIPS.tryLoad(new File(getFilesDir(), PPSOptions.HELSINKIIPPS_INDEX_FILE));

        isHAR = true;
        connectorThread.addConsumer(waitingPipeline);
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
        log.debug("Setting userId to:" + id);

        userId = id;

        String userSecret = RandomStringUtils.randomAlphanumeric(5);
        log.debug("Created new user Secret: " + userSecret);

        // Update Shared Preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, 0);
        if (settings == null) throw new IllegalStateException("Failed to load SharedPreferences");
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ID, id);
        editor.putString(PREF_SECRET, userSecret);

        editor.commit();

        doAnnotate("USER_ID SET TO: " + id);
    }

    private void doAnnotate(String tag) {
        log.debug("Adding annotation:" + tag);

        sensorQueue.push(new Tag(
                System.currentTimeMillis(),
                GlobalContext.getUserId(),
                tag));
    }

    private void doTransferSamples() {
        transferManager.doTransfer();
    }

    private void doDisableRecording() {
        connectorThread.removeConsumer(persistor);
        isRecording = false;

        final Tag stopRecordingTag = new Tag(
                System.currentTimeMillis(),
                GlobalContext.getUserId(),
                IntentAPI.VALUE_STOP_RECORDING);

        persistor.push(stopRecordingTag);

        // API EXTENSIONS are triggered on together with recording
        if (API_EXTENSIONS) {
            // Add "STOP RECORDING TAG" to publisher
            publisher.push(stopRecordingTag);
            connectorThread.removeConsumer(publisher);
            connectorThread.removeConsumer(gpsCache);
        }
    }

    private void doEnableRecording() {
        connectorThread.addConsumer(persistor);
        isRecording = true;

        final Tag tagStartRecording = new Tag(
                System.currentTimeMillis(),
                GlobalContext.getUserId(),
                IntentAPI.VALUE_START_RECORDING);

        persistor.push(tagStartRecording);

        // API EXTENSIONS are triggered on together with recording
        if (API_EXTENSIONS) {
            publisher.push(tagStartRecording);
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
        if (gpsCache == null) log.warn("gpsCache not initialized!");

        Intent intent = new Intent(ExtendedIntentAPI.RETURN_GPS_SAMPLES);
        intent.putExtra(ExtendedIntentAPI.FIELD_GPS_ENTRIES, gpsCache.getEntryString());

        sendBroadcast(intent);

        log.debug("Sent gps message " + gpsCache.getEntryString());
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
