package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.serialization.SerializationCommons;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class ProximitySerialization extends Wrapper<Proximity, Arbitrary> {
    public static final ProximitySerialization PROXIMITY_SERIALIZATION = new ProximitySerialization();

    private ProximitySerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(Proximity proximity) {
        final StringBuilder stringBuilder = new StringBuilder();

        SerializationCommons.appendString(stringBuilder, proximity.key);
        stringBuilder.append(SerializationCommons.SLASH);
        stringBuilder.append(proximity.in);
        stringBuilder.append(SerializationCommons.SLASH);
        SerializationCommons.appendString(stringBuilder, proximity.of);

        return new Arbitrary(proximity, proximity.getType(), stringBuilder.toString());
    }

    @Override
    protected Proximity invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SerializationCommons.SLASH_SEMICOLON_SEPARATED);

        final String key = SerializationCommons.nextString(scanner);
        final Boolean in = scanner.nextBoolean();
        final String of = SerializationCommons.nextString(scanner);

        return new Proximity(item, key, in, of);
    }
}
