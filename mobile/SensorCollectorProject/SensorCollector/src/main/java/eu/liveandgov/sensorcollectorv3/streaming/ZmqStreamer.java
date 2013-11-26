package eu.liveandgov.sensorcollectorv3.streaming;

import org.jeromq.ZMQ;

import eu.liveandgov.sensorcollectorv3.configuration.SensorCollectionOptions;
import eu.liveandgov.sensorcollectorv3.monitor.Monitorable;
import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;

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
        socket = ZMQ.context().socket(ZMQ.PUSH);
        socket.setHWM(1000);
    }

    @Override
    public void push(String m) {
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
