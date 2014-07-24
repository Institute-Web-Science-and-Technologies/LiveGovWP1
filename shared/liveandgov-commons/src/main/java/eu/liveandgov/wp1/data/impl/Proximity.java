package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.serialization.impl.ProximitySerialization;

/**
 * <p>Result of a proximity query</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class Proximity extends AbstractItem {
    /**
     * Key of the proximity group
     */
    public final String key;

    /**
     * True if inside of the proximity
     */
    public final boolean in;

    /**
     * Identification of the object
     */
    public final String of;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param key       Key of the proximity group
     * @param in        True if inside of the proximity
     * @param of        Identification of the object
     */
    public Proximity(long timestamp, String device, String key, boolean in, String of) {
        super(timestamp, device);
        this.key = key;
        this.in = in;
        this.of = of;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_PROXIMITY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proximity proximity = (Proximity) o;

        if (in != proximity.in) return false;
        if (key != null ? !key.equals(proximity.key) : proximity.key != null) return false;
        if (of != null ? !of.equals(proximity.of) : proximity.of != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (in ? 1 : 0);
        result = 31 * result + (of != null ? of.hashCode() : 0);
        return result;
    }

    @Override
    public String createSerializedForm() {
        return ProximitySerialization.PROXIMITY_SERIALIZATION.serialize(this);
    }
}
