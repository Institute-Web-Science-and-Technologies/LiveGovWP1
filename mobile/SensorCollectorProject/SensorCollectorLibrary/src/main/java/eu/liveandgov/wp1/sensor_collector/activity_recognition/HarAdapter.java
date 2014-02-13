package eu.liveandgov.wp1.sensor_collector.activity_recognition;

import eu.liveandgov.wp1.human_activity_recognition.HarPipeline;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI;
import eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat;
import eu.liveandgov.wp1.sensor_collector.connectors.impl.IntentEmitter;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 *
 * Created by hartmann on 10/20/13.
 */
public class HarAdapter implements eu.liveandgov.wp1.pipeline.Consumer<String> {

    private final PrefixFilter filter;
    private final MotionSensorValueProducer parseProd;
    private final Pipeline<MotionSensorValue, String> harPipeline;

    public HarAdapter(){
        // ACC filter
        filter = new PrefixFilter();
        filter.addFilter("ACC");

        // Parser
        parseProd = new MotionSensorValueProducer();
        filter.setConsumer(parseProd);

        // HAR
        harPipeline = new HarPipeline(1000);
        parseProd.setConsumer(harPipeline);

        // Multiplex samples, in order for multiple consumers to connect
        Multiplexer<String> multiplexer = new Multiplexer<String>();
        harPipeline.setConsumer(multiplexer);

        // Publish samples as Intent and as Sensor Sample.
        multiplexer.addConsumer(new IntentEmitter(IntentAPI.RETURN_ACTIVITY, IntentAPI.FIELD_ACTIVITY));
        multiplexer.addConsumer(new SampleEmitter(SsfFileFormat.SSF_ACTIVITY) );
    }

    @Override
    public void push(String m) {
        filter.push(m);
    }

}
