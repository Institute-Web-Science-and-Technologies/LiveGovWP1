package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.GoogleActivity;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GoogleActivitySerialization extends Wrapper<GoogleActivity, Arbitrary> {
    public static final GoogleActivitySerialization GOOGLE_ACTIVITY_SERIALIZATION = new GoogleActivitySerialization();

    private GoogleActivitySerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(GoogleActivity googleActivity) {
        final StringBuilder stringBuilder = new StringBuilder();

        appendString(stringBuilder, googleActivity.activity);
        stringBuilder.append(SPACE);
        stringBuilder.append(googleActivity.confidence);

        return new Arbitrary(googleActivity, googleActivity.getType(), stringBuilder.toString());
    }

    @Override
    protected GoogleActivity invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SPACE_SEPARATED);

        final String activity = nextString(scanner);
        final int confidence = scanner.nextInt();

        return new GoogleActivity(item, activity, confidence);
    }
}
