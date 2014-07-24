package eu.liveandgov.wp1.util;

/**
 * Created by Lukas Härtel on 18.03.14.
 */
public class Geo {
    /**
     * We might have this functions everywhere around this project
     */
    public static double haversine(double lat1, final double lon1, double lat2, final double lon2) {
        final double R = 6371000.785;
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
