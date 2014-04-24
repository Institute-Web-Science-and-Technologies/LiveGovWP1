package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.DataCommons;

/**
 * <p>Motion instance corresponding to the virtual linear acceleration sensor of a device</p>
 * Created by Lukas Härtel on 11.02.14.
 */
public class LinearAcceleration extends Motion {
    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param values    Sensor values
     */
    public LinearAcceleration(long timestamp, String device, float[] values) {
        super(timestamp, device, values);
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_LINEAR_ACCELERATION;
    }
}
