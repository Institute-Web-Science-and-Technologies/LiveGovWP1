package eu.liveandgov.wp1.sensor_collector.pps;

import java.util.concurrent.Executor;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.DeSerializer;
import eu.liveandgov.wp1.pipeline.impl.Detachment;
import eu.liveandgov.wp1.pipeline.impl.Serializer;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.pps.PPSPipeline;
import eu.liveandgov.wp1.pps.api.AggregatingPS;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;
import eu.liveandgov.wp1.serialization.impl.ProximitySerialization;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class PPSAdapter implements Consumer<String> {
    private final StartsWith filter;

    private final DeSerializer<GPS> deSerializer;

    private final PPSPipeline pps;

    private final Serializer<Proximity> serializer;

    private final SensorEmitter sensorEmitter;

    public PPSAdapter(String key, AggregatingPS ps) {
        filter = new StartsWith();
        filter.addPrefix(DataCommons.TYPE_GPS);

        deSerializer = new DeSerializer<GPS>(GPSSerialization.GPS_SERIALIZATION);
        filter.setConsumer(deSerializer);

        pps = new PPSPipeline(key, ps);
        deSerializer.setConsumer(pps);

        serializer = new Serializer<Proximity>(ProximitySerialization.PROXIMITY_SERIALIZATION);
        pps.setConsumer(serializer);

        sensorEmitter = new SensorEmitter();
        serializer.setConsumer(sensorEmitter);
    }

    @Override
    public void push(String s) {
        filter.push(s);
    }
}
