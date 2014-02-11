package eu.liveandgov.wp1.pps.api;

import eu.liveandgov.wp1.data.Tuple;

import java.util.LinkedList;
import java.util.List;

/**
 * Aggregating ProximityType Service
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
    public CalculationResult calculate(double lat, double lon) {
        for (ProximityService s : proximityServices) {
            final CalculationResult result = s.calculate(lat, lon);

            switch (result.type) {
                case IN_PROXIMITY:
                case NOT_IN_PROXIMITY:
                    return result;
            }
        }

        return new CalculationResult(CalculationResult.CalculationType.NO_DECISION, "");
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
