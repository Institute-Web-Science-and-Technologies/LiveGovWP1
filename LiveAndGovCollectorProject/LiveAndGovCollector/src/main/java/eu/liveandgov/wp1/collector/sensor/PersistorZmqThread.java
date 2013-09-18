package eu.liveandgov.wp1.collector.sensor;

import android.content.Context;
import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.wp1.collector.persistence.Persistor;

/**
 * Created by hartmann on 9/15/13.
 */
public class PersistorZmqThread implements Runnable {
    private static final String LOG_TAG = "P_ZMQ";
    private final Persistor persistor;
    ZMQ.Socket inSocket;
    ZMQ.Socket outSocket;

    public PersistorZmqThread(Persistor persistor){
        this.persistor = persistor;

        inSocket = ZMQ.context().socket(ZMQ.SUB);
        inSocket.subscribe("");
    }

    public void subscribe(SensorProducer SP){
        Log.i(LOG_TAG, "Subscribing to " + SP.getAddress());
        inSocket.connect(SP.getAddress());
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Persist Loop");
        String msg;
        while (true) {
            msg = inSocket.recvStr();
            Log.i("SS-REC", msg);
            persistor.push(msg);
        }
    }
}
