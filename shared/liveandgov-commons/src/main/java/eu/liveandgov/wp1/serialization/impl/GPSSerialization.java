package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.GPS;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class GPSSerialization extends Wrapper<GPS, Arbitrary> {
    public static final GPSSerialization GPS_SERIALIZATION = new GPSSerialization();

    private GPSSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(GPS gps) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(gps.lat);
        stringBuilder.append(SPACE);
        stringBuilder.append(gps.lon);

        if (gps.alt != null) {
            stringBuilder.append(SPACE);
            stringBuilder.append(gps.alt);
        }

        return new Arbitrary(gps, gps.getType(), stringBuilder.toString());
    }

    @Override
    protected GPS invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SPACE_SEPARATED);

        final double lat = scanner.nextDouble();
        final double lon = scanner.nextDouble();
        final Double alt = scanner.hasNextDouble() ? scanner.nextDouble() : null;

        return new GPS(item, lat, lon, alt);
    }
}
