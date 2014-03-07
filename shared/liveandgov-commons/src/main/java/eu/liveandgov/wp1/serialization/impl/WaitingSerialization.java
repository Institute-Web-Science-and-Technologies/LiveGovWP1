package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.serialization.SerializationCommons;
import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class WaitingSerialization extends AbstractSerialization<Waiting> {
    public static final WaitingSerialization WAITING_SERIALIZATION = new WaitingSerialization();

    private WaitingSerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, Waiting waiting) {
        SerializationCommons.appendString(stringBuilder, waiting.key);
        stringBuilder.append(SerializationCommons.SLASH);
        stringBuilder.append(waiting.duration);
        stringBuilder.append(SerializationCommons.SLASH);
        SerializationCommons.appendString(stringBuilder, waiting.at);

    }


    @Override
    protected Waiting deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SerializationCommons.SLASH_SEMICOLON_SEPARATED);

        final String key = SerializationCommons.nextString(scanner);
        final long duration = scanner.nextLong();
        final String at = SerializationCommons.nextString(scanner);

        return new Waiting(timestamp, device, key, duration, at);
    }
}
