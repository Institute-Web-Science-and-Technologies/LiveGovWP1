package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.appendString;
import static eu.liveandgov.wp1.serialization.SerializationCommons.nextString;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class ActivitySerialization extends Wrapper<Activity, Arbitrary> {
    public static final ActivitySerialization ACTIVITY_SERIALIZATION = new ActivitySerialization();

    private ActivitySerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(Activity activity) {
        final StringBuilder stringBuilder = new StringBuilder();
        appendString(stringBuilder, activity.activity);

        return new Arbitrary(activity, activity.getType(), stringBuilder.toString());
    }

    @Override
    protected Activity invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);

        final String activity = nextString(scanner);

        return new Activity(item, activity);
    }
}
