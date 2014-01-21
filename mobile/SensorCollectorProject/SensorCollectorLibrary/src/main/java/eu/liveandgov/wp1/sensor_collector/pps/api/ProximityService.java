package eu.liveandgov.wp1.sensor_collector.pps.api;

public interface ProximityService {
    /**
     * Calculates the proximity type at the given location associated with this service
     *
     * @return Returns the proximity or NO_DECISION if the proximity could not be evaluated with
     * this service
     */
    public Proximity calculate(double lon, double lat);
}
