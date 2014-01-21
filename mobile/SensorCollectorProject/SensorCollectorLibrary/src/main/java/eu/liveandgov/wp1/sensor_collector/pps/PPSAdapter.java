package eu.liveandgov.wp1.sensor_collector.pps;

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
public class PPSAdapter implements Consumer<String>, Monitorable {

    private final PrefixFilter filter;
    private final GpsSensorValueProducer parseProd;
    private final PPSPipeline ppsPipeline;

    public PPSAdapter(String key, ProximityService service) {
        // GPS filter
        filter = new PrefixFilter();
        filter.addFilter(SsfFileFormat.SSF_GPS);

        // Parser
        parseProd = new GpsSensorValueProducer();
        filter.setConsumer(parseProd);

        // PPS
        ppsPipeline = new PPSPipeline();
        ppsPipeline.setKey(key);
        ppsPipeline.setProximityService(service);
        parseProd.setConsumer(ppsPipeline);

        // Publish samples as Sensor Sample.
        ppsPipeline.setConsumer(new ConversionEmitter<ProximityEvent>(SensorSerializer.proximityEvent));
    }

    @Override
    public void push(String s) {
        filter.push(s);
    }

    @Override
    public String getStatus() {
        return ppsPipeline.getStatus();
    }
}
