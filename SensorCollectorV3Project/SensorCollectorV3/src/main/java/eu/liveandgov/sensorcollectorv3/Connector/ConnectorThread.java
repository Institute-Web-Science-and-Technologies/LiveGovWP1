package eu.liveandgov.sensorcollectorv3.Connector;

import android.util.Log;

import java.io.File;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.Persistence.Persistor;
import eu.liveandgov.sensorcollectorv3.SensorQueue.SensorQueue;

/**
 * Created by hartmann on 9/15/13.
 */
public class ConnectorThread implements Runnable {
    private static final String LOG_TAG = "ConnectorThread";

    private final SensorQueue sensorQueue;
    private final Persistor persistor;
    private final Thread thread;

    public ConnectorThread(SensorQueue sensorQueue, Persistor persistor){
        this.sensorQueue = sensorQueue;
        this.persistor = persistor;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Connection Loop");
        String msg;
        while (true) {
            msg = sensorQueue.blockingPull();
            persistor.push(msg);
        }
    }

    public void start() {
        thread.start();
    }

}
