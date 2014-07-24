package eu.liveandgov.wp1.sensor_collector.monitor;

/**
 * Interface to be implemented by objects that shall be monitored.
 *
 * Created by hartmann on 10/2/13.
 */
public interface Monitorable {
    /**
     * @return statusMessage
     */
    String getStatus();
}
