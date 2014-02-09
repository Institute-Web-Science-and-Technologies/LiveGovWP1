package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.implementation.GoogleActivity;
import eu.liveandgov.wp1.data.Item;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GoogleActivitySerialization extends Wrapper<GoogleActivity, Item<String>> {
    public static final GoogleActivitySerialization GOOGLE_ACTIVITY_SERIALIZATION = new GoogleActivitySerialization();

    private GoogleActivitySerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(GoogleActivity googleActivity) {
        final StringBuilder stringBuilder = new StringBuilder();

        appendString(stringBuilder, googleActivity.data.activity);
        stringBuilder.append(SPACE);
        stringBuilder.append(googleActivity.data.confidence);

        return new Item<String>(googleActivity.type, googleActivity.header, stringBuilder.toString());
    }

    @Override
    protected GoogleActivity invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SPACE_SEPARATED);

        final String activity = nextString(scanner);
        final int confidence = scanner.nextInt();

        return new GoogleActivity(stringItem.type, stringItem.header, new GoogleActivity.GoogleActivityStatus(activity, confidence));
    }
}
