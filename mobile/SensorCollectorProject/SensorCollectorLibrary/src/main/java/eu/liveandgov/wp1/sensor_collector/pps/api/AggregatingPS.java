package eu.liveandgov.wp1.sensor_collector.pps.api;

import java.security.spec.PSSParameterSpec;
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
    public Proximity calculate(double lon, double lat) {
        Proximity result = Proximity.NO_DECISION;

        search:
        for (ProximityService s : proximityServices) {
            result = s.calculate(lon, lat);

            switch (result) {
                case IN_PROXIMITY:
                case NOT_IN_PROXIMITY:
                    // Do not continue after full positive and full negative
                    break search;
            }
        }

        return result;
    }

    public static AggregatingPS create(ProximityService... from) {
        final AggregatingPS result = new AggregatingPS();
        for (ProximityService f : from) {
            result.getProximityServices().add(f);
        }

        return result;
    }
}
