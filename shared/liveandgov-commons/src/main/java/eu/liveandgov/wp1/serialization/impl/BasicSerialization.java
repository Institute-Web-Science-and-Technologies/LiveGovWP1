package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.serialization.Serialization;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class BasicSerialization implements Serialization<Arbitrary> {
    public static final BasicSerialization BASIC_SERIALIZATION = new BasicSerialization();

    private BasicSerialization() {

    }

    @Override
    public String serialize(Arbitrary item) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(item.getType());
        stringBuilder.append(COMMA);
        stringBuilder.append(item.getTimestamp());
        stringBuilder.append(COMMA);
        appendString(stringBuilder, item.getDevice());
        stringBuilder.append(COMMA);
        stringBuilder.append(item.value);

        return stringBuilder.toString();
    }

    @Override
    public Arbitrary deSerialize(String string) {
        final Scanner scanner = new Scanner(string);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(COMMA_SEPARATED);

        final String type = scanner.next();
        final long timestamp = scanner.nextLong();
        final String device = nextString(scanner);

        scanner.skip(COMMA_SEPARATED);

        final String line = scanner.nextLine();

        return new Arbitrary(timestamp, device, type, line);
    }
}
