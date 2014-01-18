package eu.liveandgov.wp1.sensor_collector.pps;

public interface ProximityService {
    public Proximity calculate(double lon, double lat);
}
