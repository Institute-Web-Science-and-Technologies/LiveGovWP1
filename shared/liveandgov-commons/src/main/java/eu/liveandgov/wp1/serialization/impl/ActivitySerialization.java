package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Activity;

import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.appendString;
import static eu.liveandgov.wp1.serialization.SerializationCommons.nextString;

/**
 * <p>Serialization of the activity item</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class ActivitySerialization extends AbstractSerialization<Activity> {
    /**
     * The one instance of the serialization
     */
    public static final ActivitySerialization ACTIVITY_SERIALIZATION = new ActivitySerialization();

    /**
     * Hidden constructor
     */
    protected ActivitySerialization() {
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
