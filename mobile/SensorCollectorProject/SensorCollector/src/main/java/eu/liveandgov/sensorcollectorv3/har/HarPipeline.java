package eu.liveandgov.sensorcollectorv3.har;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.MotionSensorValueProducer;
import eu.liveandgov.sensorcollectorv3.connector.PrefixFilter;

/**
 * Created by hartmann on 10/20/13.
 */
public class HarPipeline implements Consumer<String> {

    private final PrefixFilter filter;
    private final MotionSensorValueProducer parseProd;
    private final WindowProducer windowProducer;
    private final FeatureProducer featureProducer;
    private final ClassifyProducer classifyProducer;

    public HarPipeline(){
        // HAR filter
        filter = new PrefixFilter();
        filter.addFilter("ACC");

        // Parser
        parseProd = new MotionSensorValueProducer();
        filter.setConsumer(parseProd);

        // Window
        windowProducer = new WindowProducer(5000, 1000);
        parseProd.setConsumer(windowProducer);

        // Feature
        featureProducer = new FeatureProducer();
        windowProducer.setConsumer(featureProducer);

        // Classify
        classifyProducer = new ClassifyProducer();
        featureProducer.setConsumer(classifyProducer);
    }

    @Override
    public void push(String m) {
        filter.push(m);
    }
}
