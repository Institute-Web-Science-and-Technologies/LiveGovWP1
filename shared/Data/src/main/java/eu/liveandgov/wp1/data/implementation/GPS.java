package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public final class GPS extends Item<GPS.GPSStatus> {
    public static boolean isGPS(String type) {
        return DataCommons.TYPE_GPS.equals(type);
    }

    public static String assertIsGPS(String type) {
        assert isGPS(type);

        return type;
    }

    public static final class GPSStatus {
        public final double lat;

        public final double lon;

        public final Double alt;

        public GPSStatus(double lat, double lon, Double alt) {
            this.lat = lat;
            this.lon = lon;
            this.alt = alt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GPSStatus gpsStatus = (GPSStatus) o;

            if (Double.compare(gpsStatus.lat, lat) != 0) return false;
            if (Double.compare(gpsStatus.lon, lon) != 0) return false;
            if (alt != null ? !alt.equals(gpsStatus.alt) : gpsStatus.alt != null) return false;

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
            return "GPSStatus{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    ", alt=" + alt +
                    '}';
        }
    }

    public GPS(String type, Header header, GPSStatus gpsStatus) {
        super(assertIsGPS(type), header, gpsStatus);
    }
}
