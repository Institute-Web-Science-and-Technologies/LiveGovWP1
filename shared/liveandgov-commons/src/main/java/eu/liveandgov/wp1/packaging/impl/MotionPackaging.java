package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.*;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.*;

/**
 * <p>Packaging of a motion item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class MotionPackaging extends AbstractPackaging<Motion> {
    /**
     * The one instance of the packaging
     */
    public static final MotionPackaging MOTION_PACKAGING = new MotionPackaging();

    /**
     * Hidden constructor
     */
    protected MotionPackaging() {
    }

    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_Z = "z";

    @Override
    protected void pack(Map<String, Object> result, Motion item) {
        result.put(FIELD_X, item.values[0]);
        result.put(FIELD_Y, item.values[1]);
        result.put(FIELD_Z, item.values[2]);
    }

    @Override
    protected Motion unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final float x = getFloat(map.get(FIELD_X));
        final float y = getFloat(map.get(FIELD_Y));
        final float z = getFloat(map.get(FIELD_Z));

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
