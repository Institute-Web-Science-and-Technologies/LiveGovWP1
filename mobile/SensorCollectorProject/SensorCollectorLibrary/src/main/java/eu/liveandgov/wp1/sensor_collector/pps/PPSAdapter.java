package eu.liveandgov.wp1.sensor_collector.pps;

import android.util.Log;

import java.util.concurrent.Executor;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.pipeline.impl.DeSerializer;
import eu.liveandgov.wp1.pipeline.impl.Detachment;
import eu.liveandgov.wp1.pipeline.impl.Player;
import eu.liveandgov.wp1.pipeline.impl.Serializer;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;
import eu.liveandgov.wp1.pps.PPSPipeline;
import eu.liveandgov.wp1.pps.api.AggregatingPS;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;
import eu.liveandgov.wp1.serialization.impl.GPSSerialization;
import eu.liveandgov.wp1.serialization.impl.ProximitySerialization;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class PPSAdapter implements Consumer<Item> {
    private static final String LOG_TAG = "PPSA";
    private final ClassFilter<GPS> filter;

    private final Player<GPS> player;

    private final PPSPipeline pps;

    private final SensorEmitter sensorEmitter;

    public PPSAdapter(String key, AggregatingPS ps) {
        filter = new ClassFilter<GPS>(GPS.class);

        // So apparently the PPS takes quite some time, so we use a Player to play all GPS samples
        // to the pipeline on the global executor services
        player = new Player<GPS>(GlobalContext.getExecutorService(), -1, -1, 100L);
        filter.setConsumer(player);

        pps = new PPSPipeline(key, ps);
        player.setConsumer(pps);

        sensorEmitter = new SensorEmitter();
        pps.setConsumer(sensorEmitter);
    }

    @Override
    public void push(Item item) {
        filter.push(item);
    }

    @Override
    public String toString() {
        return "PPS Adapter";
    }
}
