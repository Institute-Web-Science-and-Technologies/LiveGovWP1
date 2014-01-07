package eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * Created by hartmann on 10/2/13.
 */
public interface SensorQueue extends Monitorable, Consumer<String> {

    /**
     * Pull message from queue.
     * Block if queue is empty
     *
     * @return message
     */
    String blockingPull();

}
