package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

/**
 * <p>Forwarding of the GPS item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class GPSForwarding extends AbstractForwarding<GPS> {
    /**
     * The one instance of the forwarding
     */
    public static final GPSForwarding GPS_FORWARDING = new GPSForwarding();

    /**
     * Hidden constructor
     */
    protected GPSForwarding() {
    }

    public static final String FIELD_LATITUDE = "lat";
    public static final String FIELD_LONGITUDE = "lon";
    public static final String FIELD_ALTITUDE = "alt";

    @Override
    protected void forwardRest(GPS item, Receiver target) {
        target.receive(FIELD_LATITUDE, item.lat);
        target.receive(FIELD_LONGITUDE, item.lon);

        if (item.alt != null)
            target.receive(FIELD_ALTITUDE, item.alt);
    }

    @Override
    protected GPS unForwardRest(String type, long timestamp, String device, Provider source) {
        double lat = (Double) source.provide(FIELD_LATITUDE);
        double lon = (Double) source.provide(FIELD_LONGITUDE);
        Double alt = source.contains(FIELD_ALTITUDE) ? (Double) source.provide(FIELD_ALTITUDE) : null;

        return new GPS(timestamp, device, lat, lon, alt);
    }
}
