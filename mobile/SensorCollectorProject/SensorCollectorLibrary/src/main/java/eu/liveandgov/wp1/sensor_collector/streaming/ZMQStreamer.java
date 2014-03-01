package eu.liveandgov.wp1.sensor_collector.streaming;

import android.content.SharedPreferences;
import android.util.Log;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.pipeline.impl.ZMQClient;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.R;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import zmq.ZMQ;

/**
 * String-Consumer that sends samples to a remote server using ZMQ message queue system.
 * <p/>
 * Created by hartmann on 10/2/13.
 */
public class ZMQStreamer extends ZMQClient implements Monitorable {
    {
        HWM= 2048;
    }
    public static final String LOG_TAG = "ZST";

    /**
     * Pull interval can be slow because we don't expect responses
     */
    private static final int PULL_INTERVAL = 500;

    public ZMQStreamer() {
        super(GlobalContext.getExecutorService(), PULL_INTERVAL, ZMQ.ZMQ_PUSH);

        pulled.register(new Callback<Integer>() {
            @Override
            public void call(Integer c) {
                Log.d(LOG_TAG, "ZQM Streamer pulled " + c + " responses");
            }
        });

        sent.register(new Callback<Boolean>() {
            @Override
            public void call(Boolean s) {
                if (!s)
                    Log.d(LOG_TAG, "ZMQ Streamer send failed");

            }
        });

        addressUpdated.register(new Callback<String>() {
            @Override
            public void call(String s) {
                Log.d(LOG_TAG, "ZMQ Streamer destination now " + s);
            }
        });
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
}