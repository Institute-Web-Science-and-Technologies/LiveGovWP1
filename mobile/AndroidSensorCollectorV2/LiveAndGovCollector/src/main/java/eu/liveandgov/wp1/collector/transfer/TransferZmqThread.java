package eu.liveandgov.wp1.collector.transfer;

import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.wp1.collector.persistence.Persistor;

/**
 * Created by hartmann on 9/15/13.
 */
public class TransferZmqThread implements Runnable {

    private static final String LOG_TAG = "TZMQ";
    private final Persistor p;
    private ZMQ.Context c;
    private ZMQ.Socket s;

    /**
     * Setup ZMQ Socket connection (async)
     * @param p
     */
    public TransferZmqThread(Persistor p){
        this.p = p;
        c = ZMQ.context();
        s = c.socket(ZMQ.PUSH);
        s.connect("tcp://" + TransferManagerConfig.REMOTE_HOST + ":5555");
    }


    @Override
    public void run() {
        Log.i(LOG_TAG, "Started Transfer Thread");
        String msg;
        while (true){
            msg = p.pull();
            Log.i(LOG_TAG, "pulled message " + msg);
            if (msg == null) {
                doSleep(100);
                continue;
            }
            s.send(msg);
        }
    }

    private void doSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
