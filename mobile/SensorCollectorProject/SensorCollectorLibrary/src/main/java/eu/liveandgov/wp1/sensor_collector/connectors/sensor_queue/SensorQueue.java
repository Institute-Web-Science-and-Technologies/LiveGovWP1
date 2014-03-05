package eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * Created by hartmann on 10/2/13.
 */
public interface SensorQueue extends Monitorable, Consumer<Item> {

    /**
     * Pull message from queue.
     * Block if queue is empty
     *
     * @return message
     */
    Item blockingPull();

}
