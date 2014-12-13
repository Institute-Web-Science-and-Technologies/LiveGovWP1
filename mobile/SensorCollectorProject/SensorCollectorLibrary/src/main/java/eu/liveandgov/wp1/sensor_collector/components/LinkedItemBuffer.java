package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.common.collect.Queues;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;

/**
 * <p>Item buffer based on a linked blocking queue</p>
 * Created by lukashaertel on 17.11.2014.
 */
@Singleton
public class LinkedItemBuffer implements ItemBuffer, Reporter {
    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.itemBufferLimit")
    int itemBufferLimit;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.itemBufferTimeout")
    long itemBufferTimeout;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.itemBufferTimeoutUnit")
    TimeUnit itemBufferTimeoutUnit;

    /**
     * The store for the items
     */
    private final LinkedBlockingQueue<Item> queue = Queues.newLinkedBlockingQueue();

    /**
     * Maintain a number of offer faults
     */
    private long offerFaults = 0;

    /**
     * Maintain a number of total offered items
     */
    private long offerTotal = 0;

    /**
     * Maintain a number of total polled items
     */
    private long pollTotal = 0;

    @Override
    public void offer(Item item) {
        queue.offer(item);
        offerTotal++;

        while (queue.size() > itemBufferLimit) {
            queue.poll();
            offerFaults++;
        }
    }

    @Override
    public Item poll() {
        // Return the polled item
        try {
            Item result = queue.poll(itemBufferTimeout, itemBufferTimeoutUnit);

            if (result != null)
                pollTotal++;

            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        report.putInt("size", queue.size());
        report.putInt("limit", itemBufferLimit);
        report.putLong("timeout", itemBufferTimeout);
        report.putSerializable("timeoutUnit", itemBufferTimeoutUnit);
        report.putLong("offerFaults", offerFaults);
        report.putLong("offerTotal", offerTotal);
        report.putLong("pollTotal", pollTotal);
        return report;
    }
}
