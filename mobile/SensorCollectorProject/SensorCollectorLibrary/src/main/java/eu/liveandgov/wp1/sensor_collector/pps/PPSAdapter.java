package eu.liveandgov.wp1.sensor_collector.pps;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.FunctionalPipeline;
import eu.liveandgov.wp1.pipeline.impl.StartsWithPipeline;
import eu.liveandgov.wp1.pps.PPSPipeline;
import eu.liveandgov.wp1.pps.api.AggregatingPS;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;
import eu.liveandgov.wp1.serialization.Serializations;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;
import eu.liveandgov.wp1.serialization.impl.ProximitySerialization;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class PPSAdapter implements Consumer<String> {
    private final StartsWithPipeline filter;

    private final FunctionalPipeline<String, GPS> deSerialize;

    private final PPSPipeline pps;

    private final FunctionalPipeline<Proximity, String> serialize;

    private final SensorEmitter sensorEmitter;

    public PPSAdapter(String key, AggregatingPS ps) {
        filter = new StartsWithPipeline();
        filter.getPrefixes().add(DataCommons.TYPE_GPS);

        deSerialize = new FunctionalPipeline<String, GPS>(Serializations.deSerialization(GPSSerialization.GPS_SERIALIZATION));
        filter.setConsumer(deSerialize);

        pps = new PPSPipeline(key, ps);
        deSerialize.setConsumer(pps);

        serialize = new FunctionalPipeline<Proximity, String>(Serializations.serialization(ProximitySerialization.PROXIMITY_SERIALIZATION));
        pps.setConsumer(serialize);

        sensorEmitter = new SensorEmitter();
        serialize.setConsumer(sensorEmitter);
    }

    @Override
    public void push(String s) {
        filter.push(s);
    }
}
