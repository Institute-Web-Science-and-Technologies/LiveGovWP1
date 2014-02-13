package eu.liveandgov.wp1.sensor_collector.streaming;

import android.util.Log;


import eu.liveandgov.wp1.pipeline.impl.ZMQClient;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import zmq.ZMQ;

/**
 * String-Consumer that sends samples to a remote server using ZMQ message queue system.
 * <p/>
 * Created by hartmann on 10/2/13.
 */
public class ZMQStreamer extends ZMQClient implements Monitorable {
    /**
     * Pull interval can be really slow because we don't expect responses
     */
    private static final int PULL_INTERVAL = 5000;

    public ZMQStreamer() {
        super(GlobalContext.getExecutorService(), PULL_INTERVAL, ZMQ.ZMQ_PUSH, SensorCollectionOptions.STREAMING_ZMQ_SOCKET);
    }

    @Override
    public String getStatus() {
        return "ZMQ Streaming";
    }
}