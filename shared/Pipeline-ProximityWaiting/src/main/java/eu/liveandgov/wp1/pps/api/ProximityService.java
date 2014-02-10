package eu.liveandgov.wp1.pps.api;

import eu.liveandgov.wp1.data.Tuple;

public interface ProximityService {
    /**
     * Calculates the proximityType type at the given location associated with this service
     *
     * @return Returns the proximityType or NO_DECISION if the proximityType could not be evaluated with
     * this service
     */
    public Tuple<Boolean, String> calculate(double lat, double lon);

    public boolean isUniversal();
}
