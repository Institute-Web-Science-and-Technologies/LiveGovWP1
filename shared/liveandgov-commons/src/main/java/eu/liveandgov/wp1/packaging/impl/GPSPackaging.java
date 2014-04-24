package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.GPS;

import java.util.Map;

/**
 * <p>Packaging of the GPS item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class GPSPackaging extends AbstractPackaging<GPS> {
    /**
     * The one instance of the packaging
     */
    public static final GPSPackaging GPS_PACKAGING = new GPSPackaging();

    /**
     * Hidden constructor
     */
    protected GPSPackaging() {
    }

    public static final String FIELD_LATITUDE = "lat";
    public static final String FIELD_LONGITUDE = "lon";
    public static final String FIELD_ALTITUDE = "alt";

    @Override
    protected void pack(Map<String, Object> result, GPS item) {
        result.put(FIELD_LATITUDE, item.lat);
        result.put(FIELD_LONGITUDE, item.lon);

        if (item.alt != null)
            result.put(FIELD_ALTITUDE, item.alt);
    }

    @Override
    protected GPS unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final double lat = (Double) map.get(FIELD_LATITUDE);
        final double lon = (Double) map.get(FIELD_LONGITUDE);

        final Double alt = map.containsKey(FIELD_ALTITUDE) ? (Double) map.get(FIELD_ALTITUDE) : null;

        return new GPS(timestamp, device, lat, lon, alt);
    }
}
