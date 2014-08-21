package eu.liveandgov.wp1.sensor_collector.streaming;

import android.content.SharedPreferences;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ZMQClient;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.R;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * String-Consumer that sends samples to a remote server as lines using ZMQ message queue system.
 * <p/>
 * Created by hartmann on 10/2/13.
 */
public class ZMQStreamer extends ZMQClient implements Monitorable {
    private final Logger log = LogPrincipal.get();

    /**
     * Pull interval can be slow because we don't expect responses
     */
    private static final int PULL_INTERVAL = 5000;

    public ZMQStreamer() {
        super(GlobalContext.getExecutorService(), PULL_INTERVAL, ZMQ.PUB);

        addressUpdated.register(new Callback<String>() {
            @Override
            public void call(String s) {
                log.debug( "ZMQ Streamer destination now " + s);
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
        SharedPreferences settings = GlobalContext.context.getSharedPreferences(GlobalContext.context.getString(R.string.spn), 0);

        String streamingAddressValue = settings.getString(GlobalContext.context.getString(R.string.prf_streaming_address), SensorCollectionOptions.DEFAULT_STREAMING);

        return "tcp://" + streamingAddressValue;
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