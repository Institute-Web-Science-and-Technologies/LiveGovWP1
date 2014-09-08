package eu.liveandgov.wp1.sensor_collector.os;

import eu.liveandgov.wp1.data.Item;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public interface SampleTarget {
    void handle(Item item);
}
