package eu.liveandgov.wp1.sensor_collector.pps.api;

import java.util.LinkedList;
import java.util.List;

/**
 * Aggregating Proximity Service
 *
 * @author lukashaertel
 */
public class AggregatingPS implements ProximityService {
    private final List<ProximityService> proximityServices;

    public AggregatingPS() {
        this.proximityServices = new LinkedList<ProximityService>();
    }

    public List<ProximityService> getProximityServices() {
        return proximityServices;
    }

    @Override
    public Proximity calculate(double lat, double lon) {
        Proximity result;

        for (ProximityService s : proximityServices) {
            result = s.calculate(lat, lon);

            switch (result) {
                case IN_PROXIMITY:
                case NOT_IN_PROXIMITY:
                    // Do not continue after full positive and full negative
                    return result;
            }
        }

        // Do not return error from this one
        return Proximity.NO_DECISION;
    }

    public static AggregatingPS create(ProximityService... from) {
        final AggregatingPS result = new AggregatingPS();
        for (ProximityService f : from) {
            result.getProximityServices().add(f);
        }

        return result;
    }

    @Override
    public boolean isUniversal() {
        for (ProximityService ps : proximityServices) {
            if (ps.isUniversal()) return true;
        }

        return false;
    }
}
