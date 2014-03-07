package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.MotionSerialization;

import java.util.Arrays;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public abstract class Motion extends AbstractItem {

    public final float[] values;

    public Motion(long timestamp, String device, float[] values) {
        super(timestamp, device);
        this.values = values;
    }

    public Motion(Item header, float[] values) {
        super(header);
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Motion motion = (Motion) o;

        if (!Arrays.equals(values, motion.values)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return values != null ? Arrays.hashCode(values) : 0;
    }

    @Override
    public String createSerializedForm() {
        return MotionSerialization.MOTION_SERIALIZATION.serialize(this);
    }
}
