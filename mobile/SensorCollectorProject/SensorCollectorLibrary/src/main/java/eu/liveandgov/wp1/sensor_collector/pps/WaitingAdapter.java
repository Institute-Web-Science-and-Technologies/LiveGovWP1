package eu.liveandgov.wp1.sensor_collector.pps;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.FunctionalPipeline;
import eu.liveandgov.wp1.pipeline.impl.StartsWithPipeline;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.Emitter;
import eu.liveandgov.wp1.serialization.Serializations;
import eu.liveandgov.wp1.serialization.impl.ProximitySerialization;
import eu.liveandgov.wp1.serialization.impl.WaitingSerialization;
import eu.liveandgov.wp1.waiting.WaitingPipeline;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class WaitingAdapter implements Consumer<String> {
    private final StartsWithPipeline filter;

    private final FunctionalPipeline<String, Proximity> deSerialize;

    private final WaitingPipeline waitingPipeline;

    private final FunctionalPipeline<Waiting, String> serialize;

    private final Emitter emitter;

    public WaitingAdapter(String key, long waitTreshold) {
        filter = new StartsWithPipeline();
        filter.getPrefixes().add(DataCommons.TYPE_GPS);

        deSerialize = new FunctionalPipeline<String, Proximity>(Serializations.deSerialization(ProximitySerialization.PROXIMITY_SERIALIZATION));
        filter.setConsumer(deSerialize);

        waitingPipeline = new WaitingPipeline(key, waitTreshold);
        deSerialize.setConsumer(waitingPipeline);

        serialize = new FunctionalPipeline<Waiting, String>(Serializations.serialization(WaitingSerialization.WAITING_SERIALIZATION));
        waitingPipeline.setConsumer(serialize);

        emitter = new Emitter();
        serialize.setConsumer(emitter);
    }

    @Override
    public void push(String s) {
        filter.push(s);
    }
}
