package eu.liveandgov.wp1.sensor_collector.os;

import eu.liveandgov.wp1.data.Item;

/**
 * <p>Handler of items</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface SampleTarget {
    /**
     * <p>Receives an item</p>
     *
     * @param item The item by one of the sources
     */
    void handle(Item item);
}
