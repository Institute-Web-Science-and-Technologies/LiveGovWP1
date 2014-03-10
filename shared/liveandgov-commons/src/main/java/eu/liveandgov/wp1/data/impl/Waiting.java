package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.WaitingSerialization;

/**
 * <p>Result of a waiting analysis</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class Waiting extends AbstractItem {
    /**
     * Key of the proximity group
     */
    public final String key;

    /**
     * Waiting time
     */
    @Unit("ms")
    public final long duration;

    /**
     * Identification of the object
     */
    public final String at;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param key       Key of the proximity group
     * @param duration  Waiting time
     * @param at        Identification of the object
     */
    public Waiting(long timestamp, String device, String key, long duration, String at) {
        super(timestamp, device);
        this.key = key;
        this.duration = duration;
        this.at = at;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_WAITING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Waiting waiting = (Waiting) o;

        if (duration != waiting.duration) return false;
        if (at != null ? !at.equals(waiting.at) : waiting.at != null) return false;
        if (key != null ? !key.equals(waiting.key) : waiting.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (at != null ? at.hashCode() : 0);
        return result;
    }

    @Override
    public String createSerializedForm() {
        return WaitingSerialization.WAITING_SERIALIZATION.serialize(this);
    }
}
