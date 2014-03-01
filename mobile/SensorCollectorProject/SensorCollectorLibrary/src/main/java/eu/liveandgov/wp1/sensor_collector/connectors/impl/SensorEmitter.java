package eu.liveandgov.wp1.sensor_collector.connectors.impl;

import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;

/**
 * Created by hartmann on 11/12/13.
 */
public class SensorEmitter implements Consumer<String> {
    @Override
    public void push(String value) {
        GlobalContext.getSensorQueue().push(value);
    }
}
