package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.MotionSerialization;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class Acceleration extends Motion {
    public Acceleration(long timestamp, String device, float[] values) {
        super(timestamp, device, values);
    }

    public Acceleration(Item header, float[] values) {
        super(header, values);
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_ACCELEROMETER;
    }

}
