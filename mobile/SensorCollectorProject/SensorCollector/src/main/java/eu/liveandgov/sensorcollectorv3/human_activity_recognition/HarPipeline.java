package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import eu.liveandgov.sensorcollectorv3.configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.SsfFileFormat;
import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.connectors.Pipeline;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.IntentEmitter;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.Multiplexer;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.PrefixFilter;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.SampleEmitter;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 *
 * Created by hartmann on 10/20/13.
 */
public class HarPipeline implements Consumer<String> {

    private final PrefixFilter filter;
    private final MotionSensorValueProducer parseProd;
    private final WindowProducer windowProducer;
    private final FeatureProducer featureProducer;
    private final ClassifyProducer classifyProducer;

    public HarPipeline(){
        // ACC filter
        filter = new PrefixFilter();
        filter.addFilter("ACC");

        // Parser
        parseProd = new MotionSensorValueProducer();
        filter.setConsumer(parseProd);

        // Window
        windowProducer = new WindowProducer(5000, 200);
        parseProd.setConsumer(windowProducer);

        // Feature
        featureProducer = new FeatureProducer();
        windowProducer.setConsumer(featureProducer);

        // Classify
        classifyProducer = new ClassifyProducer();
        featureProducer.setConsumer(classifyProducer);

        // Multiplex samples, in order for multiple consumers to connect
        Multiplexer<String> multiplexer = new Multiplexer<String>();
        classifyProducer.setConsumer(multiplexer);

        // Publish samples as Intent and as Sensor Sample.
        multiplexer.addConsumer(new IntentEmitter(IntentAPI.RETURN_ACTIVITY, IntentAPI.FIELD_ACTIVITY));
        multiplexer.addConsumer(new SampleEmitter(SsfFileFormat.SSF_ACTIVITY) );
    }

    @Override
    public void push(String m) {
        filter.push(m);
    }

}
