package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class Gyroscope extends Motion {
    public Gyroscope(long timestamp, String device, float[] values) {
        super(timestamp, device, values);
    }

    public Gyroscope(Item header, float[] values) {
        super(header, values);
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_GYROSCOPE;
    }
}
