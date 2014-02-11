package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class Rotation extends Motion {
    public Rotation(long timestamp, String device, float[] values) {
        super(timestamp, device, values);
    }

    public Rotation(Item header, float[] values) {
        super(header, values);
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_ROTATION;
    }
}
