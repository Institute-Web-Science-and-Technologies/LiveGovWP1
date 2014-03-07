package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.serialization.Serialization;
import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.appendString;
import static eu.liveandgov.wp1.serialization.SerializationCommons.nextString;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class ActivitySerialization extends AbstractSerialization<Activity> {
    public static final ActivitySerialization ACTIVITY_SERIALIZATION = new ActivitySerialization();

    private ActivitySerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, Activity activity) {
        appendString(stringBuilder, activity.activity);
    }


    @Override
    protected Activity deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        final String activity = nextString(scanner);

        return new Activity(timestamp, device, activity);
    }
}
