package eu.liveandgov.sensorcollectorv3.connector;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.liveandgov.sensorcollectorv3.monitor.Monitorable;
import eu.liveandgov.sensorcollectorv3.sensor_queue.SensorQueue;

/**
 * Created by hartmann on 9/15/13.
 */
public class ConnectorThread implements Runnable, Monitorable {
    private static final String LOG_TAG = "ConnectorThread";

    private final SensorQueue sensorQueue;
    private final Thread thread;

    private List<Consumer<String>> consumerList;

    private long count = 0;

    public ConnectorThread(SensorQueue sensorQueue){
        this.sensorQueue = sensorQueue;
        this.thread = new Thread(this);
        this.consumerList = new ArrayList<Consumer<String>>();
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Running Connection Loop.");
        String msg;
        while (true) {
            msg = sensorQueue.blockingPull();
            for(Consumer c : consumerList) {
                c.push(msg);
            }
            count++;
        }
    }

    public void start() {
        thread.start();
    }

    /**
     * Returns number of samples transferred by the connector
     *
     * @return count
     */
    public String getStatus(){
        return "Throughput: " + count;
    }

    public void addConsumer(Consumer c) {
        consumerList.add(c);
    }

}
