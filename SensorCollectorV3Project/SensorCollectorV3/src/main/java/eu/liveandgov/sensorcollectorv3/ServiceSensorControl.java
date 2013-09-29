package eu.liveandgov.sensorcollectorv3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.Monitor.MonitorThread;
import eu.liveandgov.sensorcollectorv3.Persistence.FilePersistor;
import eu.liveandgov.sensorcollectorv3.Persistence.PersistorThread;
import eu.liveandgov.sensorcollectorv3.Sensors.GlobalContext;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorThread;
import eu.liveandgov.sensorcollectorv3.Transfer.TransferThread;

public class ServiceSensorControl extends Service {
    static final String LOG_TAG =  "SCS";

    public boolean isRecording = false;
    public boolean isTransferring = false;

    /* ANDROID LIFECYCLE */
    @Override
    public void onCreate(){
        Log.i(LOG_TAG, "Creating ServiceSensorControl }");

        // Setup static variables
        GlobalContext.set(this);

        // Start sensor thread
        SensorThread.getInstance().start();

        PersistorThread.setup(new FilePersistor());
        PersistorThread.getInstance().start();

        // Start transfer thread
        TransferThread.setup();
        TransferThread.getInstance().start();

        // Start monitoring thread
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
        TransferThread.getInstance().trigger();
        isTransferring = !isTransferring;
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
        Intent intent = new Intent(IntentAPI.RETURN_STATUS);
        intent.putExtra(IntentAPI.FIELD_SAMPLING, isRecording);
        intent.putExtra(IntentAPI.FIELD_TRANSFERRING, isTransferring);
        sendBroadcast(intent);
    }

}
