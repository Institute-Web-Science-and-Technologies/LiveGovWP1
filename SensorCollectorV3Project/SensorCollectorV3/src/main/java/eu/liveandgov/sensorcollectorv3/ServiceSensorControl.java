package eu.liveandgov.sensorcollectorv3;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.Persistence.FilePersistor;
import eu.liveandgov.sensorcollectorv3.Persistence.Persistor;
import eu.liveandgov.sensorcollectorv3.Persistence.PersistorThread;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorSinkThread;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorThread;
import eu.liveandgov.sensorcollectorv3.Transfer.TransferThread;

public class ServiceSensorControl extends Service {
    static final String LOG_TAG =  "SCS";

    public static final String SENSOR_SOCKET = "tcp://127.0.0.1:6001";
    public static final String TRANSFER_SOCKET = "tcp://127.0.0.1:6001";
    private ZMQ.Socket sensorSocket;
    private ZMQ.Socket transferSocket;

    public boolean isRecording = false;
    public boolean isTransferring = false;


    private static ServiceSensorControl instance;

    public ServiceSensorControl() {
        super();
        instance = this;
    }

    /**
     * Handles received intents
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
        SensorThread.getInstance().unregisterSensors();
    }

    private void doEnableRecording() {
        isRecording = true;
        SensorThread.getInstance().registerSensors();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Log.i(LOG_TAG, "Creating ServiceSensorControl }");

        ZMQ.Context c = ZMQ.context();

        // Start sensor thread
        SensorThread.setupInstance(this);
        SensorThread.getInstance().start();

        // Connect Sensor Producers to Sensor Sink
        // SensorSinkThread SK = new SensorSinkThread();
        // new Thread(SK).start();

        // Persistor P = new FilePersistor(this);
        // PersistorThread PT = new PersistorThread(P);
        // new Thread(PT).start();

        // Start transfer thread
        new Thread(new TransferThread(this)).start();

        // Start monitoring thread
        // new Thread(new MonitorThread(messageHandler)).start();

        super.onCreate();
    }

    public ServiceSensorControl getInstance(){
        return instance;
    }

}
