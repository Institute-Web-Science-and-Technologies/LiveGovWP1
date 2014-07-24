package eu.liveandgov.wp1.pps.api;

/**
 * <p>Service representing a method to associate coordinates with objects</p>
 */
public interface ProximityService {
    /**
     * Calculates the proximityType type at the given location associated with this service
     *
     * @return Returns the proximityType or NO_DECISION if the proximityType could not be evaluated with
     * this service
     */
    public CalculationResult calculate(double lat, double lon);

    /**
     * True if the service is universal i.e. can map all coordinates to a result
     */
    public boolean isUniversal();
}
