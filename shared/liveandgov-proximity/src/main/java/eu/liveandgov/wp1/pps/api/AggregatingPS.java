package eu.liveandgov.wp1.pps.api;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Aggregating proximity service, uses multiple proximity services in order</p>
 *
 * @author lukashaertel
 */
public class AggregatingPS implements ProximityService {
    /**
     * The list of proximity services to use in order of application
     */
    private final List<ProximityService> proximityServices;

    /**
     * Creates a new instance
     */
    public AggregatingPS() {
        this.proximityServices = new LinkedList<ProximityService>();
    }

    /**
     * Returns a modifiable list of proximity services
     */
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

    /**
     * Creates an inline aggregating proximity service
     *
     * @param from The proximity serives to use
     * @return Returns a new aggregating proximity service
     */
    public static AggregatingPS create(ProximityService... from) {
        final AggregatingPS result = new AggregatingPS();
        for (ProximityService f : from) {
            result.getProximityServices().add(f);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * <p>Returns true if one of the proximity services is universal</p>
     */
    @Override
    public boolean isUniversal() {
        for (ProximityService ps : proximityServices) {
            if (ps.isUniversal()) return true;
        }

        return false;
    }
}
