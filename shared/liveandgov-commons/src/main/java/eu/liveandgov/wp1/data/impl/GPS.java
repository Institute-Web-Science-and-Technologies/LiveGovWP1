package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Optional;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;

/**
 * <p>GPS coordinate with optional height</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class GPS extends AbstractItem {
    /**
     * Latitude of the coordinate
     */
    @Unit("°")
    public final double lat;

    /**
     * Longitude of the coordinate
     */
    @Unit("°")
    public final double lon;

    /**
     * Height above the sealevel, may be null
     */
    @Optional
    @Unit("m")
    public final Double alt;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param lat       Latitude of the coordinate
     * @param lon       Longitude of the coordinate
     * @param alt       Height above the sealevel, may be null
     */
    public GPS(long timestamp, String device, @Unit("°") double lat, @Unit("°") double lon, @Optional @Unit("m") Double alt) {
        super(timestamp, device);
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_GPS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GPS gps = (GPS) o;

        if (Double.compare(gps.lat, lat) != 0) return false;
        if (Double.compare(gps.lon, lon) != 0) return false;
        if (alt != null ? !alt.equals(gps.alt) : gps.alt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (alt != null ? alt.hashCode() : 0);
        return result;
    }

    @Override
    public String createSerializedForm() {
        return GPSSerialization.GPS_SERIALIZATION.serialize(this);
    }
}
