package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.Serialization;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class BasicSerialization implements Serialization<Item<String>> {
    public static final BasicSerialization BASIC_SERIALIZATION = new BasicSerialization();

    private BasicSerialization() {

    }

    @Override
    public String serialize(Item<String> item) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(item.type);
        stringBuilder.append(COMMA);
        stringBuilder.append(item.header.timestamp);
        stringBuilder.append(COMMA);
        appendString(stringBuilder, item.header.device);
        stringBuilder.append(COMMA);
        stringBuilder.append(item.data);

        return stringBuilder.toString();
    }

    @Override
    public Item<String> deSerialize(String string) {
        final Scanner scanner = new Scanner(string);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(COMMA_SEPARATED);

        final String type = scanner.next();
        final long timestamp = scanner.nextLong();
        final String device = nextString(scanner);

        scanner.skip(COMMA_SEPARATED);

        final String line = scanner.nextLine();

        return new Item<String>(type, new Header(timestamp, device), line);
    }
}
