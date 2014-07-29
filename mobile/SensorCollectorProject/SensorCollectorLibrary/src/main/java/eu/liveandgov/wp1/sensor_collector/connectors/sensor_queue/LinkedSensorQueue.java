package eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * Simple queue class of a fixed maximal capacity.
 * If the capacity is reached further messages are dropped.
 * The class provides a blockinPull() method, that blocks until new messages are available.
 * <p/>
 * Created by hartmann on 9/29/13.
 */
public class LinkedSensorQueue implements SensorQueue {
    public static final int CAPACITY = 1024;

    private final BlockingQueue<Item>queue = new LinkedBlockingQueue<Item>();

    /**
     * Push message to the queue.
     * Drop message if queue is full.
     *
     * @param item
     */
    @Override
    public void push(Item item) {
        if (queue.size() < CAPACITY) {
            try {
                queue.put(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public Item blockingPull() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public String getStatus() {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        stringBuilder.append("Queue Size: ");
        stringBuilder.append(queue.size());

        return stringBuilder.toString();
    }
}
