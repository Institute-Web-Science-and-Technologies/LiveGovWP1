package eu.liveandgov.wp1.sensor_collector.waiting;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.ConversionEmitter;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.FromSSFConverter;
import eu.liveandgov.wp1.sensor_collector.connectors.implementations.PrefixFilter;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.sensor_collector.pps.ProximityEvent;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class WaitingAdapter implements Consumer<String>, Monitorable {

    private final PrefixFilter filter;
    private final FromSSFConverter<ProximityEvent> parseProd;
    private final WaitingPipeline waitingPipeline;

    public WaitingAdapter(String key, long waitTreshold) {
        // Proximity filter
        filter = new PrefixFilter();
        filter.addFilter(SsfFileFormat.SSF_PPROXIMITY);

        // Parser
        parseProd = new FromSSFConverter<ProximityEvent>(SensorSerializer.proximityEvent);
        filter.setConsumer(parseProd);

        // WPL
        waitingPipeline = new WaitingPipeline(key, waitTreshold);
        parseProd.setConsumer(waitingPipeline);

        // Publish samples as Sensor Sample.
        waitingPipeline.setConsumer(new ConversionEmitter<WaitingEvent>(SensorSerializer.waitingEvent));
    }

    @Override
    public void push(final String s) {
        filter.push(s);
    }

    @Override
    public String getStatus() {
        return waitingPipeline.getStatus();
    }
}
