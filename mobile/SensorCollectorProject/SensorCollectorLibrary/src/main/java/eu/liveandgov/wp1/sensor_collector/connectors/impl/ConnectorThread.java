package eu.liveandgov.wp1.sensor_collector.connectors.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.liveandgov.wp1.pipeline.MultiProducer;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * Thread that actively polls a blocking queue and sends samples to a list of consumers.
 * <p/>
 * Created by hartmann on 9/15/13.
 */
public class ConnectorThread extends MultiProducer<String> implements Runnable, Monitorable {
    private static final String LOG_TAG = "CT";

    private final SensorQueue sensorQueue;
    private final Thread thread;


    private long messageCount = 0;

    public ConnectorThread(SensorQueue sensorQueue) {
        this.sensorQueue = sensorQueue;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Connection Loop.");
        while (true) {
            produce(sensorQueue.blockingPull());
            messageCount++;
        }
    }

    public void start() {
        thread.start();
    }

    /**
     * Returns number of samples transferred by the connector
     *
     * @return messageCount
     */
    @Override
    public String getStatus() {
        return "Throughput: " + messageCount;
    }
}
