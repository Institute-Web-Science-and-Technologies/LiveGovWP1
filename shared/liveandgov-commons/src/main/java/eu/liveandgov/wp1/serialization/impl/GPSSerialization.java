package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.GPS;

import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.SPACE;
import static eu.liveandgov.wp1.serialization.SerializationCommons.SPACE_SEPARATED;

/**
 * <p>Serialization of the GPS item</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class GPSSerialization extends AbstractSerialization<GPS> {
    /**
     * The one instance of the serialization
     */
    public static final GPSSerialization GPS_SERIALIZATION = new GPSSerialization();

    /**
     * Hidden constructor
     */
    protected GPSSerialization() {
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
