package eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simple queue class of a fixed maximal capacity.
 * If the capacity is reached further messages are dropped.
 * The class provides a blockinPull() method, that blocks until new messages are available.
 * <p/>
 * Created by hartmann on 9/29/13.
 */
public class LinkedSensorQueue implements SensorQueue {
    public static final int capacity = 1000;

    private int size = 0;
    public final Queue<String> Q = new ConcurrentLinkedQueue<String>();

    /**
     * Push message to the queue.
     * Drop message if queue is full.
     *
     * @param m
     */
    @Override
    public void push(String m) {
        if (size + 1 < capacity) {
            size++;
            Q.offer(m);
        }
    }

    private String pull() {
        if (size > 0) {
            size--;
            return Q.poll();
        } else {
            return null;
        }
    }

    @Override
    public String blockingPull() {
        String m;

        while (true) {
            m = pull();

            if (m != null) break;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return m;
    }

    @Override
    public String getStatus() {
        return "Queue Size: " + Q.size();
    }
}
