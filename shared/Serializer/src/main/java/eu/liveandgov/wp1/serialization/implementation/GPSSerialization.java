package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.implementation.GPS;
import eu.liveandgov.wp1.data.Item;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class GPSSerialization extends Wrapper<GPS, Item<String>> {
    public static final GPSSerialization GPS_SERIALIZATION = new GPSSerialization();

    private GPSSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(GPS gps) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(gps.data.lat);
        stringBuilder.append(SPACE);
        stringBuilder.append(gps.data.lon);

        if (gps.data.alt != null) {
            stringBuilder.append(SPACE);
            stringBuilder.append(gps.data.alt);
        }

        return new Item<String>(gps.type, gps.header, stringBuilder.toString());
    }

    @Override
    protected GPS invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SPACE_SEPARATED);

        final double lat = scanner.nextDouble();
        final double lon = scanner.nextDouble();
        final Double alt = scanner.hasNextDouble() ? scanner.nextDouble() : null;

        return new GPS(stringItem.type, stringItem.header, new GPS.GPSStatus(lat, lon, alt));
    }
}
