package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.serialization.impl.VelocitySerialization;

/**
 * Created by Lukas Härtel on 16.07.2014.
 */
public class Velocity extends AbstractItem {
    public final float velocity;

    public Velocity(long timestamp, String device, float velocity) {
        super(timestamp, device);
        this.velocity = velocity;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_VELOCITY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Velocity velocity1 = (Velocity) o;

        if (Float.compare(velocity1.velocity, velocity) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (velocity != +0.0f ? Float.floatToIntBits(velocity) : 0);
    }

    @Override
    protected String createSerializedForm() {
        return VelocitySerialization.VELOCITY_SERIALIZATION.serialize(this);
    }
}
