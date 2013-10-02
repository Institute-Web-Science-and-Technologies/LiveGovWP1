package eu.liveandgov.sensorcollectorv3.Persistence;

import org.jeromq.ZMQ;

import java.io.File;

import eu.liveandgov.sensorcollectorv3.Configuration.SensorCollectionOptions;

/**
 * Created by hartmann on 10/2/13.
 */
public class ZmqStreamer implements Persistor {
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
            socket.connect(SensorCollectionOptions.UPLOAD_ZMQ_SOCKET);
            isConnected = true;
        }
        socket.send(m);
    }

    @Override
    public boolean exportSamples(File stageFile) {
        return false;
    }

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }
}
