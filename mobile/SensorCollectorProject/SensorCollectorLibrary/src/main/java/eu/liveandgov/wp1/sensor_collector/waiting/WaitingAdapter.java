package eu.liveandgov.wp1.sensor_collector.waiting;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.DeSerializer;
import eu.liveandgov.wp1.pipeline.impl.Serializer;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;
import eu.liveandgov.wp1.serialization.impl.ProximitySerialization;
import eu.liveandgov.wp1.serialization.impl.WaitingSerialization;
import eu.liveandgov.wp1.waiting.WaitingPipeline;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class WaitingAdapter implements Consumer<String> {
    private final StartsWith filter;

    private final DeSerializer<Proximity> deSerializer;

    private final WaitingPipeline waitingPipeline;

    private final Serializer<Waiting> serializer;

    private final SensorEmitter sensorEmitter;

    public WaitingAdapter(String key, long waitTreshold) {
        filter = new StartsWith();
        filter.addPrefix(DataCommons.TYPE_GPS);

        deSerializer = new DeSerializer<Proximity>(ProximitySerialization.PROXIMITY_SERIALIZATION);
        filter.setConsumer(deSerializer);

        waitingPipeline = new WaitingPipeline(key, waitTreshold);
        deSerializer.setConsumer(waitingPipeline);

        serializer = new Serializer<Waiting>(WaitingSerialization.WAITING_SERIALIZATION);
        waitingPipeline.setConsumer(serializer);

        sensorEmitter = new SensorEmitter();
        serializer.setConsumer(sensorEmitter);
    }

    @Override
    public void push(String s) {
        filter.push(s);
    }
}
