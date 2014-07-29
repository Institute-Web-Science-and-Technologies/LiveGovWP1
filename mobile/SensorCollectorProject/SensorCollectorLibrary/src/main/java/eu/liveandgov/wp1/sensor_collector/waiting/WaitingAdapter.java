package eu.liveandgov.wp1.sensor_collector.waiting;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;
import eu.liveandgov.wp1.waiting.WaitingPipeline;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class WaitingAdapter implements Consumer<Item> {
    private final ClassFilter<Proximity> filter;

    private final WaitingPipeline waitingPipeline;

    private final SensorEmitter sensorEmitter;

    public WaitingAdapter(String key, long waitTreshold) {
        filter = new ClassFilter<Proximity>(Proximity.class);

        waitingPipeline = new WaitingPipeline(key, waitTreshold);
        filter.setConsumer(waitingPipeline);

        sensorEmitter = new SensorEmitter();
        waitingPipeline.setConsumer(sensorEmitter);
    }

    @Override
    public void push(Item item) {
        filter.push(item);
    }

    @Override
    public String toString() {
        return "Waiting Adapter";
    }
}
