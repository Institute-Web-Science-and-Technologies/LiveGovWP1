package eu.liveandgov.wp1.sensor_collector.pps;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.ClassFilter;
import eu.liveandgov.wp1.pipeline.impl.Player;
import eu.liveandgov.wp1.pps.PPSPipeline;
import eu.liveandgov.wp1.pps.api.ProximityService;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.SensorEmitter;

/**
 * Created by lukashaertel on 11.02.14.
 */
public class PPSAdapter implements Consumer<Item> {
    private static final String LOG_TAG = "PPSA";
    private final ClassFilter<GPS> filter;

    private final Player<GPS> player;

    private final PPSPipeline pps;

    private final SensorEmitter sensorEmitter;

    public PPSAdapter(String key, ProximityService ps) {
        filter = new ClassFilter<GPS>(GPS.class);

        // So apparently the PPS takes quite some time, so we use a Player to play all GPS samples
        // to the pipeline on the global executor services, assuming that the last 16 will be
        // sufficient, as GPS is only occurring about once a second
        player = new Player<GPS>(GlobalContext.getExecutorService(), 16);
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
