package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class MagneticField extends Motion {
    public MagneticField(long timestamp, String device, float[] values) {
        super(timestamp, device, values);
    }

    public MagneticField(Item header, float[] values) {
        super(header, values);
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_MAGNETOMETER;
    }
}
