package eu.liveandgov.sensorcollectorv3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.Monitor.MonitorThread;
import eu.liveandgov.sensorcollectorv3.Connector.ConnectorThread;
import eu.liveandgov.sensorcollectorv3.Persistence.FilePersistor;
import eu.liveandgov.sensorcollectorv3.Persistence.Persistor;
import eu.liveandgov.sensorcollectorv3.SensorQueue.LinkedSensorQueue;
import eu.liveandgov.sensorcollectorv3.SensorQueue.SensorQueue;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorThread;
import eu.liveandgov.sensorcollectorv3.Transfer.TransferManager;
import eu.liveandgov.sensorcollectorv3.Transfer.TransferThreadPost;

public class ServiceSensorControl extends Service {
    static final String LOG_TAG =  "SCS";
    public static final String STAGE_FILENAME = "sensor.stage.ssf";
    public static final String SENSOR_FILENAME = "sensor.ssf";

    public boolean isRecording = false;
    public boolean isTransferring = false;

    public ServiceSensorControl() {}

    // Communication Channels
    SensorQueue sensorQueue;
    Persistor   persistor;

    TransferManager transferManager;

    /* ANDROID LIFECYCLE */
    @Override
    public void onCreate(){
        Log.i(LOG_TAG, "Creating ServiceSensorControl }");

        // Setup static variables
        GlobalContext.set(this);

        // setup communication Channels
        persistor   = new FilePersistor(
                new File(GlobalContext.context.getFilesDir(), SENSOR_FILENAME));

        sensorQueue = new LinkedSensorQueue();

        // Start sensor thread
        SensorThread.setup(sensorQueue);
        SensorThread.getInstance().start();

        // Connect sensorQueue to Persistor
        ConnectorThread.setup(sensorQueue, persistor);
        ConnectorThread.getInstance().start();

        // setup sensor manager
        transferManager = new TransferThreadPost(persistor,
                new File(GlobalContext.context.getFilesDir(), STAGE_FILENAME));

        // Start transfer thread
        // TransferThreadZMQ.setup();
        // TransferThreadZMQ.getInstance().start();

        // Start monitoring thread
        // MonitorThread.addQueue(sensorQueue)
        // MonitorThread.addPersistor(persistor)
        MonitorThread.getInstance().start();

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
        Log.i(LOG_TAG, "Received intent with action " + action);

        if (action == null) return START_STICKY;

        // Dispatch IntentAPI
        if (action.equals(IntentAPI.RECORDING_ENABLE)) {
            doEnableRecording();
            doSendStatus();
        } else if (action.equals(IntentAPI.RECORDING_DISABLE)) {
            doDisableRecording();
            doSendStatus();
        } else if (action.equals(IntentAPI.TRANSFER_SAMPLES)) {
            doTransferSamples();
            doSendStatus();
        } else if (action.equals(IntentAPI.ANNOTATE)) {
            doAnnotate();
        } else if (action.equals(IntentAPI.GET_STATUS)) {
            doSendStatus();
        } else {
            Log.i(LOG_TAG, "Received unknown action " + action);
        }

        return START_STICKY;
    }

    private void doAnnotate() {
    }

    private void doTransferSamples() {
        transferManager.doTransfer();
        isTransferring = true;
    }

    private void doDisableRecording() {
        SensorThread.getInstance().stopAllRecording();
        isRecording = false;
    }

    private void doEnableRecording() {
        SensorThread.getInstance().startAllRecording();
        isRecording = true;
    }

    private void doSendStatus() {
        isTransferring = TransferThreadPost.getInstance().isTransferring();

        Intent intent = new Intent(IntentAPI.RETURN_STATUS);
        intent.putExtra(IntentAPI.FIELD_SAMPLING, isRecording);
        intent.putExtra(IntentAPI.FIELD_TRANSFERRING, isTransferring);
        sendBroadcast(intent);
    }

}
