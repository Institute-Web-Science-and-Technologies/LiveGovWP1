package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.implementation.Activity;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class ActivitySerialization extends Wrapper<Activity, Item<String>> {
    public static final ActivitySerialization ACTIVITY_SERIALIZATION = new ActivitySerialization();

    private ActivitySerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(Activity activity) {
        final StringBuilder stringBuilder = new StringBuilder();
        appendString(stringBuilder, activity.data);

        return new Item<String>(activity.type, activity.header, activity.data);
    }

    @Override
    protected Activity invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);

        final String activity = nextString(scanner);

        return new Activity(stringItem.type, stringItem.header, activity);
    }
}
