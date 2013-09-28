package eu.liveandgov.sensorcollectorv3.ApplicationComponents;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.Persistor;
import eu.liveandgov.sensorcollectorv3.SensorThread;
import eu.liveandgov.sensorcollectorv3.TransferThread;

public class ServiceSensorControl extends Service {
    static final String LOG_TAG =  "SCS";

    private boolean isRecording = false;
    private boolean isTransferring = false;

    public static Persistor P;
    public static TransferThread T;

    /**
     * Handles received intents
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        doSendLog("Hello LogView");

        return START_STICKY;
    }

    private void doSendStatus() {
        Intent intent = new Intent(IntentAPI.RETURN_STATUS);
        intent.putExtra(IntentAPI.FIELD_SAMPLING, isRecording);
        intent.putExtra(IntentAPI.FIELD_TRANSFERRING, isTransferring);
        sendBroadcast(intent);
    }

    private void doSendLog(String message){
        Intent intent = new Intent(IntentAPI.RETURN_LOG);
        intent.putExtra(IntentAPI.FIELD_LOG, message);
        sendBroadcast(intent);
    }

    private void doAnnotate() {
    }

    private void doTransferSamples() {
        isTransferring = !isTransferring;
    }

    private void doDisableRecording() {
        isRecording = false;
    }

    private void doEnableRecording() {
        isRecording = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Log.i(LOG_TAG, "Creating ServiceSensorControl }");

        if (true) return;

        // Start sensor thread
        new Thread(new SensorThread(this)).start();

        // Start transfer thread
        T = new TransferThread(P);
        new Thread(T).start();

        // Start monitoring thread
        Handler H = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                // setText((String) inputMessage.getData().get("msg"));
            }
        };
        // new Thread(new MonitorThread(H)).start();

        super.onCreate();
    }

}
