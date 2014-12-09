package eu.liveandgov.wp1.sensor_collector.os;

/**
 * <p>Source for sample type</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface SampleSource {
    /**
     * <p>Activates provision of samples for this sample source</p>
     */
    void activate();

    /**
     * <p>Checks if provision of samples is active</p>
     *
     * @return Returns true if active
     */
    boolean isActive();

    /**
     * <p>Deactivates provision of samples for this sample source</p>
     */
    void deactivate();
}
