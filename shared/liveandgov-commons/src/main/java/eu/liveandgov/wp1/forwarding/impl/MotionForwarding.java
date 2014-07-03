package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.getFloat;

/**
 * <p>Forwarding of a motion item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class MotionForwarding extends AbstractForwarding<Motion> {
    /**
     * The one instance of the forwarding
     */
    public static final MotionForwarding MOTION_FORWARDING = new MotionForwarding();

    /**
     * Hidden constructor
     */
    protected MotionForwarding() {
    }

    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_Z = "z";

    @Override
    protected void forwardRest(Motion item, Receiver target) {
        target.receive(FIELD_X, item.values[0]);
        target.receive(FIELD_Y, item.values[1]);
        target.receive(FIELD_Z, item.values[2]);
    }

    @Override
    protected Motion unForwardRest(String type, long timestamp, String device, Provider source) {
        float x = (Float) source.provide(FIELD_X);
        float y = (Float) source.provide(FIELD_Y);
        float z = (Float) source.provide(FIELD_Z);

        final float[] values = {x, y, z};

        if (DataCommons.TYPE_ACCELEROMETER.equals(type)) {
            return new Acceleration(timestamp, device, values);
        } else if (DataCommons.TYPE_LINEAR_ACCELERATION.equals(type)) {
            return new LinearAcceleration(timestamp, device, values);
        } else if (DataCommons.TYPE_GRAVITY.equals(type)) {
            return new Gravity(timestamp, device, values);
        } else if (DataCommons.TYPE_GYROSCOPE.equals(type)) {
            return new Gyroscope(timestamp, device, values);
        } else if (DataCommons.TYPE_MAGNETOMETER.equals(type)) {
            return new MagneticField(timestamp, device, values);
        } else if (DataCommons.TYPE_ROTATION.equals(type)) {
            return new Rotation(timestamp, device, values);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
