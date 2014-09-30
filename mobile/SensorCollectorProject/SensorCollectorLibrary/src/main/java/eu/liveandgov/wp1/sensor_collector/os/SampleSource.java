package eu.liveandgov.wp1.sensor_collector.os;

/**
 * <p>Source for sample type</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface SampleSource {
    void activate();

    boolean isActive();

    void deactivate();
}
