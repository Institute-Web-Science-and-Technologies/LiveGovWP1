package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.implementation.GPS;
import eu.liveandgov.wp1.data.implementation.Proximity;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class ProximitySerialization extends Wrapper<Proximity, Item<String>> {
    public static final ProximitySerialization PROXIMITY_SERIALIZATION = new ProximitySerialization();

    private ProximitySerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(Proximity proximity) {
        final StringBuilder stringBuilder = new StringBuilder();

        appendString(stringBuilder, proximity.data.key);
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(proximity.data.proximityType));
        stringBuilder.append(SLASH);
        appendString(stringBuilder, proximity.data.objectIdentifier);

        return new Item<String>(proximity.type, proximity.header, stringBuilder.toString());
    }

    @Override
    protected Proximity invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final String key = nextString(scanner);
        final Proximity.ProximityType proximityType = fromText(Proximity.ProximityType.class, nextString(scanner));
        final String objectIdentifier = nextString(scanner);

        return new Proximity(stringItem.type, stringItem.header, new Proximity.ProximityStatus(key, proximityType, objectIdentifier));
    }
}
