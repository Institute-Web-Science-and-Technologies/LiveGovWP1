package eu.liveandgov.wp1.sensor_miner.streaming;

import android.util.Log;

import org.jeromq.ZContext;
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

    public final static int MESSAGE_RETRY_COUNT = 10;

    private final ZContext context;

    private ZMQ.Socket socket;

    public ZmqStreamer(){
        context = new ZContext();
        context.setHWM(1000);
    }

    @Override
    public void push(String m)
    {
        for(int i=0;i<MESSAGE_RETRY_COUNT;i++)
        {
            if(socket == null)
            {
                socket = context.createSocket(ZMQ.PUSH);
                socket.connect(SensorCollectionOptions.STREAMING_ZMQ_SOCKET);
            }

            if(!socket.send(m, ZMQ.DONTWAIT))
            {
                Log.w(LOG_TAG, "ZMQ connection transmission failed, invalidating socket");

                context.destroySocket(socket);
                socket = null;
            }

            if(socket != null)
            {
                return;
            }
        }
    }

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }
}
