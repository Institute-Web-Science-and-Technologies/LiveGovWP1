package eu.liveandgov.wp1.collector.sensor;

import android.content.Context;
import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.wp1.collector.persistence.Persistor;

/**
 * Created by hartmann on 9/15/13.
 */
public class SensorSink implements Runnable {
    private static final String LOG_TAG = "SSK";
    private final Context context;
    ZMQ.Socket inSocket;
    ZMQ.Socket outSocket;

    public SensorSink(Context context){
        this.context = context;

        inSocket = ZMQ.context().socket(ZMQ.SUB);
        inSocket.subscribe("");

        outSocket = ZMQ.context().socket(ZMQ.PUSH);
        outSocket.setHWM(10); // only buffer 10 messages
        outSocket.connect("tcp://141.26.71.84:5555");
    }

    public void subscribe(SensorProducer SP){
        Log.i(LOG_TAG, "Subscribing to " + SP.getAddress());
        inSocket.connect(SP.getAddress());
    }

    @Override
    public void run() {
//        Persistor P = new Persistor(context);
        Log.i(LOG_TAG, "Running Persist Loop");
        String msg;
        while (true) {
            msg = inSocket.recvStr();
            Log.i("SS-REC", msg);
//            P.push(msg);

            //outSocket.send(msg);
        }

    }
}