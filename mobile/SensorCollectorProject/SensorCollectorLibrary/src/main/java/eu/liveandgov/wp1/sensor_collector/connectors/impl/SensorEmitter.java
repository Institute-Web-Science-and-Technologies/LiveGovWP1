package eu.liveandgov.wp1.sensor_collector.connectors.impl;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;

/**
 * Created by hartmann on 11/12/13.
 */
public class SensorEmitter implements Consumer<Item> {
    @Override
    public void push(Item item) {
        GlobalContext.getSensorQueue().push(item);
    }
}
