package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.packaging.Packaging;
import eu.liveandgov.wp1.packaging.PackagingCommons;

import java.util.Map;
import java.util.TreeMap;

import static eu.liveandgov.wp1.packaging.PackagingCommons.*;

/**
 * <p>Abstract serialization formulates the basic packaging and un-packaging that have to be completed</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public abstract class AbstractPackaging<Data extends Item> implements Packaging<Data> {
    @Override
    public Map<String, ?> pack(Data data) {
        final Map<String, Object> result = new TreeMap<String, Object>();

        result.put(PackagingCommons.FIELD_TYPE, data.getType());
        result.put(PackagingCommons.FIELD_TIMESTAMP, data.getTimestamp());
        result.put(PackagingCommons.FIELD_DEVICE, data.getDevice());

        packRest(result, data);

        return result;
    }


    /**
     * Writes the relevant data of the item to the map builder
     *
     * @param result The map to write to
     * @param item   The item of which to write the relevant data, i.e. not type, timestamp or device
     */
    protected abstract void packRest(Map<String, Object> result, Data item);


    @Override
    public Data unPack(Map<String, ?> map) {
        final String type = (String) map.get(FIELD_TYPE);
        final long timestamp = (Long) map.get(FIELD_TIMESTAMP);
        final String device = (String) map.get(FIELD_DEVICE);

        return unPackRest(type, timestamp, device, map);
    }


    /**
     * Reads the relevant data of the map into the resulting item
     *
     * @param type      The type that has been prepared
     * @param timestamp The time that has been prepared
     * @param device    The device that has been prepared
     * @param map       The map to read the relevant data from, i.e. not type, timestamp or device
     * @return Returns the resulting item
     */
    protected abstract Data unPackRest(String type, long timestamp, String device, Map<String, ?> map);
}
