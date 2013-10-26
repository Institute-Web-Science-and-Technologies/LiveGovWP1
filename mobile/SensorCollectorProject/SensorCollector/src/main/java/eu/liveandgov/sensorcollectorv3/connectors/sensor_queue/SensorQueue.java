package eu.liveandgov.sensorcollectorv3.connectors.sensor_queue;

import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.monitor.Monitorable;

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
