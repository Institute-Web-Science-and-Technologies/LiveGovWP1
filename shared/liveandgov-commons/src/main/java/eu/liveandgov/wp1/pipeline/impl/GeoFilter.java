package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.util.Geo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Provides methods for filter GPS data based on a set of predicates</p>
 * Created by Lukas Härtel on 18.03.14.
 */
public class GeoFilter extends Pipeline<GPS, GPS> {
    /**
     * This degree value specifies the minimum distance for a point to be removed if requested
     */
    @Unit("°")
    public static final double REMOVE_DEGREE_EPSILON = 1e-7;

    /**
     * Spheres that are to be included or excluded based on radius sign
     */
    private final Set<Triple<Double, Double, Double>> spherical = new HashSet<Triple<Double, Double, Double>>();

    /**
     * Adds a filter around the given coordinates, specify a negative radius to reject points in a radius
     *
     * @param lat    The center latitude of the filter
     * @param lon    The center longitude of the filter
     * @param radius The radius of the filter, negative for exclusion filters
     */
    public void addSpherical(@Unit("°") double lat, @Unit("°") double lon, @Unit("m") double radius) {
        spherical.add(Triple.create(lat, lon, radius));
    }

    /**
     * Removes an explicit spherical filter
     * Adds a filter around the given coordinates, specify a negative radius to reject points in a radius
     *
     * @param lat    The center latitude of the filter
     * @param lon    The center longitude of the filter
     * @param radius The radius of the filter
     */
    public void removeSpherical(@Unit("°") double lat, @Unit("°") double lon, @Unit("m") double radius) {
        spherical.remove(Triple.create(lat, lon, radius));
    }

    /**
     * Removes all filters at this longitude and latitude
     *
     * @param lat The center latitude of the filters
     * @param lon The center longitude of the filters
     */
    public void removeSphericals(@Unit("°") double lat, @Unit("°") double lon) {
        final Iterator<Triple<Double, Double, Double>> si = spherical.iterator();

        while (si.hasNext()) {
            final Triple<Double, Double, Double> s = si.next();

            if (Geo.haversine(s.left, s.center, lat, lon) <= REMOVE_DEGREE_EPSILON)
                si.remove();
        }
    }

    @Override
    public void push(GPS gps) {
        // Go over all spherical filters
        for (Triple<Double, Double, Double> s : spherical) {
            // Calculate distance
            final double distance = Geo.haversine(s.left, s.center, gps.lat, gps.lon);

            // If negative, this radius is an exclusion
            if (s.right < 0) {
                // Stop test if inside of this radius
                if (distance <= -s.right) return;
            } else {
                // Else, its an inclusion, stop if outside of this radius
                if (distance > s.right) return;
            }
        }

        // No rejection occurred, produce the item
        produce(gps);
    }
}
