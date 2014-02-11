package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.*;

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

        if (DataCommons.TYPE_ACCELEROMETER.equals(item.getType())) {
            return new Acceleration(item, values);
        } else if (DataCommons.TYPE_LINEAR_ACCELERATION.equals(item.getType())) {
            return new LinearAcceleration(item, values);
        } else if (DataCommons.TYPE_GRAVITY.equals(item.getType())) {
            return new Gravity(item, values);
        } else if (DataCommons.TYPE_GYROSCOPE.equals(item.getType())) {
            return new Gyroscope(item, values);
        } else if (DataCommons.TYPE_MAGNETOMETER.equals(item.getType())) {
            return new MagneticField(item, values);
        } else if (DataCommons.TYPE_ROTATION.equals(item.getType())) {
            return new Rotation(item, values);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
