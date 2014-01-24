package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.connectors.MultiProducer;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * Thread that actively polls a blocking queue and sends samples to a list of consumers.
 * <p/>
 * Created by hartmann on 9/15/13.
 */
public class ConnectorThread implements Runnable, Monitorable, MultiProducer<String> {
    private static final String LOG_TAG = "CT";

    private final SensorQueue sensorQueue;
    private final Thread thread;

    // Remark: consumerList is modified by other threads.
    private List<Consumer<String>> consumerList = new CopyOnWriteArrayList<Consumer<String>>();
    private List<Callback> onEmptyList = new ArrayList<Callback>();
    private List<Callback> onNonEmptyList = new ArrayList<Callback>();

    private long messageCount = 0;

    public ConnectorThread(SensorQueue sensorQueue) {
        this.sensorQueue = sensorQueue;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Connection Loop.");
        String msg;
        while (true) {
            msg = sensorQueue.blockingPull();
            synchronized (consumerList) {
                for (Consumer<String> c : consumerList) {
                    c.push(msg);
                }
            }
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

    @Override
    public void addConsumer(Consumer<String> c) {
        if (consumerList.isEmpty()) callAll(onNonEmptyList);

        if (consumerList.contains(c)) {
            Log.w(LOG_TAG, "Consumer already connected: " + c.toString());
            return;
        }

        consumerList.add(c);
    }


    @Override
    public boolean removeConsumer(Consumer<String> c) {
        boolean ret = consumerList.remove(c);

        if (consumerList.isEmpty()) callAll(onEmptyList);
        return ret;
    }


    private void callAll(List<Callback> callbackList) {
        Log.d(LOG_TAG, "Callbacks triggered: " + callbackList.size());
        for (Callback c : callbackList) {
            if (c == null) continue;
            c.call();
        }
    }

    public void registerNonEmptyCallback(Callback c) {
        if (c == null) return;
        onNonEmptyList.add(c);
    }

    public void registerEmptyCallback(Callback c) {
        onEmptyList.add(c);
    }

    public static interface Callback {
        public void call();
    }
}
