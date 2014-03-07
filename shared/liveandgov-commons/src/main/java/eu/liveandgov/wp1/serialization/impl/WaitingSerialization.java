package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.serialization.SerializationCommons;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class WaitingSerialization extends Wrapper<Waiting, Arbitrary> {
    public static final WaitingSerialization WAITING_SERIALIZATION = new WaitingSerialization();

    private WaitingSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(Waiting waiting) {
        final StringBuilder stringBuilder = new StringBuilder();

        SerializationCommons.appendString(stringBuilder, waiting.key);
        stringBuilder.append(SerializationCommons.SLASH);
        stringBuilder.append(waiting.duration);
        stringBuilder.append(SerializationCommons.SLASH);
        SerializationCommons.appendString(stringBuilder, waiting.at);

        return new Arbitrary(waiting, waiting.getType(), stringBuilder.toString());
    }

    @Override
    protected Waiting invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SerializationCommons.SLASH_SEMICOLON_SEPARATED);

        final String key = SerializationCommons.nextString(scanner);
        final long duration = scanner.nextLong();
        final String at = SerializationCommons.nextString(scanner);

        return new Waiting(item, key, duration, at);
    }
}
