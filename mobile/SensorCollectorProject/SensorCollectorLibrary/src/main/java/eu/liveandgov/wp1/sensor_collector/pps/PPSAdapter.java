package eu.liveandgov.wp1.sensor_collector.pps;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.ConversionEmitter;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.PrefixFilter;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityService;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class PPSAdapter implements Consumer<String> {

    private final ScheduledExecutorService executor;

    private final PrefixFilter filter;
    private final GpsSensorValueProducer parseProd;
    private final PPSPipeline ppsPipeline;

    public PPSAdapter(String key, ProximityService service) {
        executor = new ScheduledThreadPoolExecutor(1);

        // GPS filter
        filter = new PrefixFilter();
        filter.addFilter(SsfFileFormat.SSF_GPS);

        // Parser
        parseProd = new GpsSensorValueProducer();
        filter.setConsumer(parseProd);

        // PPS
        ppsPipeline = new PPSPipeline(key, service);
        parseProd.setConsumer(ppsPipeline);

        // Publish samples as Sensor Sample.
        ppsPipeline.setConsumer(new ConversionEmitter<ProximityEvent>(SensorSerializer.proximityEvent));
    }

    @Override
    public void push(final String s) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                filter.push(s);
            }
        });
    }
}
