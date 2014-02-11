package eu.liveandgov.wp1.pps.api;

public interface ProximityService {
    /**
     * Calculates the proximityType type at the given location associated with this service
     *
     * @return Returns the proximityType or NO_DECISION if the proximityType could not be evaluated with
     * this service
     */
    public CalculationResult calculate(double lat, double lon);

    public boolean isUniversal();
}
