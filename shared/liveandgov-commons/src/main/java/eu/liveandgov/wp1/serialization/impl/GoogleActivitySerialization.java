package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.GoogleActivity;

import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * <p>Serialization of the google play activity item</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class GoogleActivitySerialization extends AbstractSerialization<GoogleActivity> {
    /**
     * The one instance of the serialization
     */
    public static final GoogleActivitySerialization GOOGLE_ACTIVITY_SERIALIZATION = new GoogleActivitySerialization();

    /**
     * Hidden constructor
     */
    protected GoogleActivitySerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, GoogleActivity googleActivity) {
        appendString(stringBuilder, googleActivity.activity);
        stringBuilder.append(SPACE);
        stringBuilder.append(googleActivity.confidence);
    }


    @Override
    protected GoogleActivity deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SPACE_SEPARATED);

        final String activity = nextString(scanner);
        final int confidence = scanner.nextInt();

        return new GoogleActivity(timestamp, device, activity, confidence);
    }

}
