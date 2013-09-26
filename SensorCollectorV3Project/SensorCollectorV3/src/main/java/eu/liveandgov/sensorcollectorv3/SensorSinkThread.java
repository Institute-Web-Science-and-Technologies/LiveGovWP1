package eu.liveandgov.sensorcollectorv3;

import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.SensorProducers.Producer;

/**
 * Created by hartmann on 9/22/13.
 */
public class SensorSinkThread implements Runnable {
    private static final String LOG_TAG = "SSK";
    private ZMQ.Socket inSocket;
    private ZMQ.Socket outSocket;

    private String outAddress = "tcp://127.0.0.1:6000";

    public SensorSinkThread(){
        Log.i(LOG_TAG, "Setup");
        inSocket = ZMQ.context().socket(ZMQ.SUB);
        inSocket.subscribe(""); // subsribe to all topics

        outSocket = ZMQ.context().socket(ZMQ.PUSH);
        outSocket.bind(outAddress);
    }

    public String getOutAddress() { return outAddress; }

    public void subscribeTo(Producer sensorProducer) {
        Log.i(LOG_TAG, "Subsrubing to " + sensorProducer.getAddress());
        inSocket.connect(sensorProducer.getAddress());
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Starting Sensor Sink");
        String msg;
        while (true) {
            msg = inSocket.recvStr();
            outSocket.send(msg);
            MonitorThread.sampleCount += 1;
        }
    }
}

