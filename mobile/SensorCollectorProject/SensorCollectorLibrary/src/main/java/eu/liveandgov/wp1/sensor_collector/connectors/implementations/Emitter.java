package eu.liveandgov.wp1.sensor_collector.connectors.implementations;

import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;

/**
 * Created by hartmann on 11/12/13.
 */
public class Emitter implements Consumer<String> {
    private final SensorQueue sensorQueue = GlobalContext.getSensorQueue();

    @Override
    public void push(String value) {
        sensorQueue.push(value);
    }

}
