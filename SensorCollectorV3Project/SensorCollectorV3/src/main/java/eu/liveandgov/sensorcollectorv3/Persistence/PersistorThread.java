package eu.liveandgov.sensorcollectorv3.Persistence;

import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.Persistence.Persistor;
import eu.liveandgov.sensorcollectorv3.Sensors.SensorSinkThread;

/**
 * Created by hartmann on 9/15/13.
 */
public class PersistorThread implements Runnable {
    private static final String LOG_TAG = "P_ZMQ";
    private final Persistor persistor;

    ZMQ.Socket inSocket;

    public PersistorThread(Persistor persistor){
        this.persistor = persistor;
        inSocket = ZMQ.context().socket(ZMQ.PULL);
    }

    public void connect(SensorSinkThread SK){
        Log.i(LOG_TAG, "Connecting to " + SK.getOutAddress());
        inSocket.connect(SK.getOutAddress());
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Persist Loop");
        String msg;
        while (true) {
            msg = inSocket.recvStr();
            // Log.i("STORING", msg);
            persistor.push(msg);
        }
    }
}
