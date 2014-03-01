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
    public static final int capacity = 4096;

    public final Queue<String> queue = new ConcurrentLinkedQueue<String>();

    /**
     * Push message to the queue.
     * Drop message if queue is full.
     *
     * @param m
     */
    @Override
    public void push(String m) {
        queue.offer(m);
        while (queue.size() >= capacity) {
            queue.poll();
        }
    }

    private String pull() {
        return queue.poll();
    }

    @Override
    public String blockingPull() {
        String m;

        while (true) {
            m = pull();

            if (m != null) break;

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return m;
    }

    @Override
    public String getStatus() {
        return "Queue Size: " + queue.size();
    }
}
