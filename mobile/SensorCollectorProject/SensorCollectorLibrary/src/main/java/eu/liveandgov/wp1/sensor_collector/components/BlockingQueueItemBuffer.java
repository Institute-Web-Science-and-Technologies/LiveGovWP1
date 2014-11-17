package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.inject.Singleton;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;

/**
 * Created by lukashaertel on 17.11.2014.
 */
@Singleton
public class BlockingQueueItemBuffer implements ItemBuffer, Reporter {
    public static final int CAPACITY = 1024;

    private final BlockingQueue<Item> queue = new LinkedBlockingQueue<Item>();

    @Override
    public void offer(Item item) {
        if (queue.size() < CAPACITY) {
            try {
                queue.put(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    @Override
    public Item pull() {
        try {
            return queue.take();
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
        report.putInt("capacity", CAPACITY);
        return report;
    }
}
