package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public class Gravity extends Motion {
    public Gravity(long timestamp, String device, float[] values) {
        super(timestamp, device, values);
    }

    public Gravity(Item header, float[] values) {
        super(header, values);
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_GRAVITY;
    }

}
