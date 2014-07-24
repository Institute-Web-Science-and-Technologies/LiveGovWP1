package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.Serialization;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * <p>Abstract serialization formulates the basic serialization and de-serialization that have to be completed</p>
 * Created by Lukas Härtel on 07.03.14.
 */
public abstract class AbstractSerialization<Data extends Item> implements Serialization<Data> {

    @Override
    public String serialize(Data item) {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        stringBuilder.append(item.getType());
        stringBuilder.append(COMMA);
        stringBuilder.append(item.getTimestamp());
        stringBuilder.append(COMMA);
        appendString(stringBuilder, item.getDevice());
        stringBuilder.append(COMMA);

        serializeRest(stringBuilder, item);

        return stringBuilder.toString();
    }

    /**
     * Writes the relevant data of the item to the string builder
     *
     * @param stringBuilder The string builder to write to
     * @param item          The item of which to write the relevant data, i.e. not type, timestamp or device
     */
    protected abstract void serializeRest(StringBuilder stringBuilder, Data item);

    @Override
    public Data deSerialize(String string) {
        final Scanner scanner = new Scanner(string);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(COMMA_SEPARATED);

        final String type = scanner.next();
        final long timestamp = scanner.nextLong();
        final String device = nextString(scanner);

        scanner.skip(COMMA_SEPARATED);

        return deSerializeRest(type, timestamp, device, scanner);
    }

    /**
     * Reads the relevant data of the string into the resulting item
     *
     * @param type      The type that has been prepared
     * @param timestamp The time that has been prepared
     * @param device    The device that has been prepared
     * @param scanner   The scanner to read the relevant data from, i.e. not type, timestamp or device
     * @return Returns the resulting item
     */
    protected abstract Data deSerializeRest(String type, long timestamp, String device, Scanner scanner);
}
