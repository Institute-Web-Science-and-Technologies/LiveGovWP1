package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.SPACE;
import static eu.liveandgov.wp1.serialization.SerializationCommons.SPACE_SEPARATED;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class GPSSerialization extends AbstractSerialization<GPS> {
    public static final GPSSerialization GPS_SERIALIZATION = new GPSSerialization();

    private GPSSerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, GPS gps) {
        stringBuilder.append(gps.lat);
        stringBuilder.append(SPACE);
        stringBuilder.append(gps.lon);

        if (gps.alt != null) {
            stringBuilder.append(SPACE);
            stringBuilder.append(gps.alt);
        }
    }


    @Override
    protected GPS deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SPACE_SEPARATED);

        final double lat = scanner.nextDouble();
        final double lon = scanner.nextDouble();
        final Double alt = scanner.hasNextDouble() ? scanner.nextDouble() : null;

        return new GPS(timestamp, device, lat, lon, alt);
    }
}
