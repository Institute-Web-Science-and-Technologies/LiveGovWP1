package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GPS extends AbstractItem {
    public final double lat;

    public final double lon;

    public final Double alt;

    public GPS(long timestamp, String device, double lat, double lon, Double alt) {
        super(timestamp, device);
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public GPS(Item header, double lat, double lon, Double alt) {
        super(header);
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
    public String toString() {
        return "GPS{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                '}';
    }

    @Override
    public String createSerializedForm() {
        return GPSSerialization.GPS_SERIALIZATION.serialize(this);
    }
}
