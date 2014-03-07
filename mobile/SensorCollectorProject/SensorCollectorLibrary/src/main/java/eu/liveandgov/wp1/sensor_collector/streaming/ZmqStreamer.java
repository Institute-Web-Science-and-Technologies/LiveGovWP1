package eu.liveandgov.wp1.sensor_collector.streaming;



import android.util.Log;

import org.jeromq.ZMQ;

import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;

/**
 * String-Consumer that sends samples to a remote server using ZMQ message queue system.
 *
 * Created by hartmann on 10/2/13.
 */
public class ZmqStreamer implements Monitorable, Consumer<String> {
    public final static String LOG_TAG = "ZMQStreamer";

    private final ZMQ.Socket socket;
    private boolean isConnected = false;

    public ZmqStreamer(){
        socket = ZMQ.context().socket(ZMQ.PUB);
        socket.setHWM(1000);
    }

    @Override
    public void push(String m) {
        Log.i("ZMQSTREAMER", m);
        // Lazy build up connection.
        // Constructor is called on main thread. No connection is possible there.
        if (!isConnected) {
            socket.connect(SensorCollectionOptions.STREAMING_ZMQ_SOCKET);
            isConnected = true;
        }
        socket.send(m);
    }

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }
}