package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.implementation.Waiting;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class WaitingSerialization extends Wrapper<Waiting, Item<String>> {
    public static final WaitingSerialization WAITING_SERIALIZATION = new WaitingSerialization();

    private WaitingSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(Waiting waiting) {
        final StringBuilder stringBuilder = new StringBuilder();

        appendString(stringBuilder, waiting.data.key);
        stringBuilder.append(SLASH);
        stringBuilder.append(waiting.data.waitingTime);
        stringBuilder.append(SLASH);
        appendString(stringBuilder, waiting.data.objectIdentifier);

        return new Item<String>(waiting.type, waiting.header, stringBuilder.toString());
    }

    @Override
    protected Waiting invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final String key = nextString(scanner);
        final long waitingTime = scanner.nextLong();
        final String objectIdentifier = nextString(scanner);

        return new Waiting(stringItem.type, stringItem.header, new Waiting.WaitingStatus(key, waitingTime, objectIdentifier));
    }
}
