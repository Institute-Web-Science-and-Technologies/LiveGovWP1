package eu.liveandgov.wp1.sensor_collector.components;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;

/**
 * <p>The item buffer is the central element maintained by the operating system to accept new items.</p>
 * Created by lukashaertel on 17.11.2014.
 */
public interface ItemBuffer extends Reporter {
    /**
     * Offers an item to the item buffer, may discard older entries
     *
     * @param item The new item
     */
    public void offer(Item item);

    /**
     * Polls an item, may wait a timeout time and return null if nothing left in buffer
     *
     * @return The item or null
     */
    public Item poll();
}
