package eu.liveandgov.sensorcollectorv3.har;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.Pipeline;
import eu.liveandgov.sensorcollectorv3.connector.Producer;

/**
 * Created by hartmann on 10/20/13.
 */
public class HarPipeline extends Pipeline<String,String> {

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
        windowProducer = new WindowProducer(5000, 1000);
        parseProd.setConsumer(windowProducer);

        // Feature
        featureProducer = new FeatureProducer();
        windowProducer.setConsumer(featureProducer);

        // Classify
        classifyProducer = new ClassifyProducer();
        featureProducer.setConsumer(classifyProducer);

        // REMARK:
        // using classifyProduces.setConsumer(consumer) does not work here,
        // since consumer = EmptyConsumer at this point in time.
    }

    @Override
    public void push(String m) {
        filter.push(m);
    }

    @Override
    public void setConsumer(Consumer<String> consumer){
        // Subscribe to outputs of our own consumer
        classifyProducer.setConsumer(consumer);
    }

}
