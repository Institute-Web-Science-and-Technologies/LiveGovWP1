package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.implementation.Motion;
import static eu.liveandgov.wp1.serialization.SerializationCommons.*;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class MotionSerialization extends Wrapper<Motion, Item<String>> {
    public static final MotionSerialization MOTION_SERIALIZATION = new MotionSerialization();

    private MotionSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(Motion motion) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (motion.data.length > 0) {
            stringBuilder.append(motion.data[0]);

            for (int i = 1; i < motion.data.length; i++) {
                stringBuilder.append(SPACE);
                stringBuilder.append(motion.data[i]);
            }
        }

        return new Item<String>(motion.type, motion.header, stringBuilder.toString());
    }

    @Override
    protected Motion invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SPACE_SEPARATED);

        final List<Float> floatList = new ArrayList<Float>();
        while (scanner.hasNextFloat()) {
            floatList.add(scanner.nextFloat());
        }

        final float[] result = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            result[i] = floatList.get(i);
        }

        return new Motion(stringItem.type, stringItem.header, result);
    }
}
