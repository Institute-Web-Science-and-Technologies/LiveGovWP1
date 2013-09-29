package eu.liveandgov.sensorcollectorv3.Persistence;

import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.Sensors.MessageQueue;

/**
 * Created by hartmann on 9/15/13.
 */
public class PersistorThread implements Runnable {
    private static final String LOG_TAG = "P_ZMQ";

    private final Persistor persistor;
    private final Thread thread;

    private static PersistorThread instance;

    /* Signleton Pattern */
    private PersistorThread(Persistor persistor){
        this.persistor = persistor;
        this.thread = new Thread(this);
    }

    public static void setup(Persistor persistor) {
        instance = new PersistorThread(persistor);
    }

    public static PersistorThread getInstance(){
        if (instance == null) instance = new PersistorThread(new FilePersistor());
        return instance;
    }


    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Persist Loop");
        String msg;
        while (true) {
            msg = MessageQueue.blockingPull();
            Log.i("STORING", msg);
            persistor.push(msg);
        }
    }

    public void start() {
        thread.start();
    }

    public long getSize() {
        return persistor.getSize();
    }

    public Persistor getPersistor() {
        return persistor;
    }
}
