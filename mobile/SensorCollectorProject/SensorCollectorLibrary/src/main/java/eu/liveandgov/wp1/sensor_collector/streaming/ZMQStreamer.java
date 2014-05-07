package eu.liveandgov.wp1.sensor_collector.streaming;

import android.content.SharedPreferences;
import android.util.Log;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ZMQClient;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.R;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.util.LocalBuilder;

import org.zeromq.ZMQ;

/**
 * String-Consumer that sends samples to a remote server as lines using ZMQ message queue system.
 * <p/>
 * Created by hartmann on 10/2/13.
 */
public class ZMQStreamer extends ZMQClient implements Monitorable {

    public static final String LOG_TAG = "ZST";

    /**
     * Pull interval can be slow because we don't expect responses
     */
    private static final int PULL_INTERVAL = 5000;

    public ZMQStreamer() {
        super(GlobalContext.getExecutorService(), PULL_INTERVAL, ZMQ.PUB);

        addressUpdated.register(new Callback<String>() {
            @Override
            public void call(String s) {
                Log.d(LOG_TAG, "ZMQ Streamer destination now " + s);
            }
        });
    }

    @Override
    protected void configure(ZMQ.Socket socket) {
        super.configure(socket);

        // Reconnect starts with half a second
        socket.setReconnectIVL(500L);
        // Maximum rate is fifteen seconds
        socket.setReconnectIVLMax(15L * 1000L);
    }

    @Override
    public void push(String s) {
        StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        stringBuilder.append(s);
        stringBuilder.append("\r\n");

        super.push(stringBuilder.toString());
    }

    @Override
    protected String getAddress() {
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

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }

    @Override
    public String toString() {
        return "ZMQ Streamer";
    }

    public final Consumer<Item> itemNode = new Consumer<Item>() {
        @Override
        public void push(Item item) {
            ZMQStreamer.this.push(item.toSerializedForm());
        }

        @Override
        public String toString() {
            return ZMQStreamer.this.toString() + ".itemNode";
        }
    };
}