package eu.liveandgov.wp1.sensor_collector.serial;

import com.google.inject.Singleton;

import eu.liveandgov.wp1.data.Item;

/**
 * <p>
 * Serializes an item by the old SSF
 * </p>
 * <p>
 * Created on 09.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
@Singleton
public class SSFSerialzier implements ItemSerializer {
    @Override
    public String serialize(Item item) {
        return item.toSerializedForm();
    }
}
