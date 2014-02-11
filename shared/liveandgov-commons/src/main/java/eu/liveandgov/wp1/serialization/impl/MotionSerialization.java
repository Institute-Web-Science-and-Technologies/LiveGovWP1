package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.Motion;

import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.serialization.SerializationCommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class MotionSerialization extends Wrapper<Motion, Arbitrary> {
    public static final MotionSerialization MOTION_SERIALIZATION = new MotionSerialization();

    private MotionSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(Motion motion) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (motion.values.length > 0) {
            stringBuilder.append(motion.values[0]);

            for (int i = 1; i < motion.values.length; i++) {
                stringBuilder.append(SerializationCommons.SPACE);
                stringBuilder.append(motion.values[i]);
            }
        }

        return new Arbitrary(motion, motion.getType(), stringBuilder.toString());
    }

    @Override
    protected Motion invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SerializationCommons.SPACE_SEPARATED);

        final List<Float> floatList = new ArrayList<Float>();
        while (scanner.hasNextFloat()) {
            floatList.add(scanner.nextFloat());
        }

        final float[] values = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            values[i] = floatList.get(i);
        }

        final Motion.Type type;
        if (DataCommons.TYPE_ACCELEROMETER.equals(item.getType())) {
            type = Motion.Type.ACCELEROMETER;
        } else if (DataCommons.TYPE_LINEAR_ACCELERATION.equals(item.getType())) {
            type = Motion.Type.LINEAR_ACCELERATION;
        } else if (DataCommons.TYPE_GRAVITY.equals(item.getType())) {
            type = Motion.Type.GRAVITY;
        } else if (DataCommons.TYPE_GYROSCOPE.equals(item.getType())) {
            type = Motion.Type.GYROSCOPE;
        } else if (DataCommons.TYPE_MAGNETOMETER.equals(item.getType())) {
            type = Motion.Type.MAGNETOMETER;
        } else if (DataCommons.TYPE_ROTATION.equals(item.getType())) {
            type = Motion.Type.ROTATION;
        } else {
            throw new IllegalArgumentException();
        }


        return new Motion(item, type, values);
    }
}
