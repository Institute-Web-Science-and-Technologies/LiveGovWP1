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

        for (ProximityService s : proximityServices) {
            result = s.calculate(lon, lat);

            if (result != Proximity.NO_DECISION) break;
        }

        return result;
    }

    public static AggregatingPS create(ProximityService... from)
    {
        final AggregatingPS result = new AggregatingPS();
        for(ProximityService f : from)
        {
            result.getProximityServices().add(f);
        }

        return result;
    }
}
