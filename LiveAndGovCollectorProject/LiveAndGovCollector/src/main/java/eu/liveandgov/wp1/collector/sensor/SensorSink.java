package eu.liveandgov.wp1.collector.sensor;

import android.util.Log;

import org.jeromq.ZMQ;

/**
 * Created by hartmann on 9/15/13.
 */
public class SensorSink implements Runnable {
    private static final String LOG_TAG = "SSK";

    ZMQ.Socket inSocket;
    ZMQ.Socket outSocket;

    public SensorSink(){
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
        Log.i(LOG_TAG, "Running Log Loop");
        String msg;
        while (true) {
            msg = inSocket.recvStr();
            // Log.i("SS-REC", msg);
            outSocket.send(msg);
        }

    }
}
