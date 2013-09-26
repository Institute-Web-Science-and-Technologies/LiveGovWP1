package eu.liveandgov.sensorcollectorv3;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;

public class ServiceSensorControl extends Service {
    static final String LOG_TAG =  "SCS";
    static boolean firstRun = true;

    public static Persistor P;
    public static TransferThread T;

    /**
     * Handles received intents
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.i(LOG_TAG, "Received intent with action " + action);

        if (action.equals(IntentAPI.SAMPLING_ENABLE)) {
            doEnableSampling();
        } else if (action.equals(IntentAPI.SAMPLING_DISABLE)) {
            doStopService();
        } else if (action.equals(IntentAPI.TRANSFER_SAMPLES)) {
            doTransferSamples();
        } else if (action.equals(IntentAPI.ANNOTATE)) {
            doSendAnnotation();
        } else if (action.equals(IntentAPI.GET_STATUS)) {
            doSendStatus();
        } else {
            Log.i(LOG_TAG, "Received unknown action " + action);
        }
        return START_STICKY;
    }

    private void doSendStatus() {
    }

    private void doSendAnnotation() {
    }

    private void doTransferSamples() {
    }

    private void doStopService() {
    }

    private void doEnableSampling() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Log.i(LOG_TAG, "Creating ServiceSensorControl }");

        // Start sensor thread
        P = new FilePersistor(this);
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
