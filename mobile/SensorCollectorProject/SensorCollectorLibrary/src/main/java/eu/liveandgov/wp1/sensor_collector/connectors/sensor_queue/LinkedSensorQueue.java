package eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import eu.liveandgov.wp1.data.Item;

/**
 * Simple queue class of a fixed maximal capacity.
 * If the capacity is reached further messages are dropped.
 * The class provides a blockinPull() method, that blocks until new messages are available.
 * <p/>
 * Created by hartmann on 9/29/13.
 */
public class LinkedSensorQueue implements SensorQueue {
    public static final int capacity = 4096;

    public final Queue<Item> queue = new ConcurrentLinkedQueue<Item>();

    /**
     * Push message to the queue.
     * Drop message if queue is full.
     *
     * @param item
     */
    @Override
    public void push(Item item) {
        if (queue.size() + 1 < capacity) {
            queue.offer(item);
        }
    }

    private Item pull() {
        return queue.poll();
    }

    @Override
    public Item blockingPull() {
        Item m;

        while (true) {
            m = pull();

            if (m != null) break;

            try {
                Thread.sleep(100);
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
