package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.serialization.SerializationCommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Serialization of a motion item</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class MotionSerialization extends AbstractSerialization<Motion> {
    /**
     * The one instance of the serialization
     */
    public static final MotionSerialization MOTION_SERIALIZATION = new MotionSerialization();

    /**
     * Hidden constructor
     */
    protected MotionSerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, Motion motion) {
        if (motion.values.length > 0) {
            stringBuilder.append(motion.values[0]);

            for (int i = 1; i < motion.values.length; i++) {
                stringBuilder.append(SerializationCommons.SPACE);
                stringBuilder.append(motion.values[i]);
            }
        }
    }

    @Override
    protected Motion deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SerializationCommons.SPACE_SEPARATED);

        final List<Float> floatList = new ArrayList<Float>();
        while (scanner.hasNextFloat()) {
            floatList.add(scanner.nextFloat());
        }

        final float[] values = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            values[i] = floatList.get(i);
        }

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
