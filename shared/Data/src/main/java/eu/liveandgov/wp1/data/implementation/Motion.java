package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class Motion extends Item<float[]> {
    public static boolean isMotion(String type) {
        return DataCommons.TYPE_ACCELEROMETER.equals(type)
                | DataCommons.TYPE_LINEAR_ACCELERATION.equals(type)
                | DataCommons.TYPE_GRAVITY.equals(type)
                | DataCommons.TYPE_GYROSCOPE.equals(type)
                | DataCommons.TYPE_MAGNETOMETER.equals(type)
                | DataCommons.TYPE_ROTATION.equals(type);
    }

    public static String assertIsMotion(String type) {
        assert isMotion(type);

        return type;
    }

    public Motion(String type, Header header, float[] floats) {
        super(assertIsMotion(type), header, floats);
    }
}
