package eu.liveandgov.wp1.sensor_collector.components;

import com.google.inject.Singleton;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;

/**
 * Created by lukashaertel on 17.11.2014.
 */
public interface ItemBuffer extends Reporter {
    public void offer(Item item);

    public Item pull();
}
