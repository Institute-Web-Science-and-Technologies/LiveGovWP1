package eu.liveandgov.wp1.sensor_miner.streaming;

import android.util.Log;

import org.jeromq.ZMQ;
import org.jeromq.ZMQException;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_miner.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_miner.monitor.Monitorable;

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
            if(socket.connect(SensorCollectionOptions.STREAMING_ZMQ_SOCKET))
            {
                isConnected = true;
            }
            else
            {
                Log.d(LOG_TAG, "Could not connect streamer");
            }
        }

        // If successfully initialized, try to send
        if (isConnected)
        {
            if(!socket.send(m, ZMQ.NOBLOCK))
            {
                Log.d(LOG_TAG, "Could not send data");
            }
        }
    }

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }
}
