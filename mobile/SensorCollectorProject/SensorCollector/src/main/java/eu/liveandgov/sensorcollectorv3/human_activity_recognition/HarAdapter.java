package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import eu.liveandgov.sensorcollectorv3.configuration.IntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.SsfFileFormat;
import eu.liveandgov.sensorcollectorv3.connectors.Pipeline;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.IntentEmitter;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.Multiplexer;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.PrefixFilter;
import eu.liveandgov.sensorcollectorv3.connectors.implementations.SampleEmitter;
import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.containers.MotionSensorValue;
import eu.liveandgov.wp1.feature_pipeline.producers.ClassifyProducer;
import eu.liveandgov.wp1.feature_pipeline.producers.FeatureProducer;
import eu.liveandgov.wp1.feature_pipeline.producers.WindowProducer;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 *
 * Created by hartmann on 10/20/13.
 */
public class HarAdapter implements Consumer<String> {

    private final PrefixFilter filter;
    private final MotionSensorValueProducer parseProd;
    private final WindowProducer windowProducer;
    private final FeatureProducer featureProducer;
    private final ClassifyProducer classifyProducer;
//    private final Pipeline<MotionSensorValue, String> harPipeline;

    public HarAdapter(){
        // ACC filter
        filter = new PrefixFilter();
        filter.addFilter("ACC");

        // Parser
        parseProd = new MotionSensorValueProducer();
        filter.setConsumer(parseProd);

        // HAR
//        harPipeline = new HarPipeline();
//        harPipeline.setConsumer(parseProd);

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
