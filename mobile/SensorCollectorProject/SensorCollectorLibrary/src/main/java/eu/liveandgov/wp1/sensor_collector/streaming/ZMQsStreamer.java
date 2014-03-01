package eu.liveandgov.wp1.sensor_collector.streaming;

import android.content.SharedPreferences;
import android.util.Log;

import org.jeromq.ZContext;
import org.jeromq.ZMQ;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.R;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * String-Consumer that sends samples to a remote server using ZMQ message queue system.
 * <p/>
 * Created by hartmann on 10/2/13.
 */
public class ZMQStreamer implements Monitorable, Consumer<String> {
    public final static String LOG_TAG = "ZMQStreamer";

    public final static int MESSAGE_RETRY_COUNT = 10;

    private final ZContext context;

    private ZMQ.Socket socket;

    public ZMQStreamer() {
        context = new ZContext();
        context.setHWM(1000);
    }

    private static String getAddress() {
        final SharedPreferences settings = GlobalContext.context.getSharedPreferences(GlobalContext.context.getString(R.string.spn), 0);

        final String streamingAddressValue = settings.getString(GlobalContext.context.getString(R.string.prf_streaming_address), null);

        if (streamingAddressValue == null)
            return SensorCollectionOptions.STREAMING_ZMQ_SOCKET;
        else {
            if (streamingAddressValue.matches("[^:]+:\\d+"))
                return "tcp://" + streamingAddressValue;
            else
                return "tcp://" + streamingAddressValue + ":5555";
        }
    }

    private String lcon = null;

    @Override
    public void push(String m) {
        for (int i = 0; i < MESSAGE_RETRY_COUNT; i++) {
            if (socket == null) {
                socket = context.createSocket(ZMQ.PUSH);
                socket.connect(lcon = getAddress());
            } else {
                String ncon = getAddress();

                if (!lcon.equals(ncon)) {
                    socket.connect(lcon = ncon);
                }
            }

            if (!socket.send(m + "\r\n", ZMQ.DONTWAIT)) {
                Log.w(LOG_TAG, "ZMQ connection transmission failed, invalidating socket");

                context.destroySocket(socket);
                socket = null;
            }

            if (socket != null) {
                return;
            }
        }
    }

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }
}