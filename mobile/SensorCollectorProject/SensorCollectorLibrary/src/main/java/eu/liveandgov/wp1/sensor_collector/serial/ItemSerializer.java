package eu.liveandgov.wp1.sensor_collector.serial;

import eu.liveandgov.wp1.data.Item;

/**
 * <p>
 * </p>
 * <p>
 * Created on 09.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
public interface ItemSerializer {
    public String serialize(Item item);
}
