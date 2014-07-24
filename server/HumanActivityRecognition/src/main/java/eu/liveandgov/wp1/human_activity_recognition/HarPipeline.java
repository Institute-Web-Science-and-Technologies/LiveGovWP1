package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.human_activity_recognition.producers.ClassifyProducer;
import eu.liveandgov.wp1.human_activity_recognition.producers.FeatureProducer;
import eu.liveandgov.wp1.human_activity_recognition.producers.WindowProducer;

/**
 * Pipeline class that consumes accelerometer values and produces an activity stream.
 *
 * Created by hartmann on 10/20/13.
 */
public class HarPipeline extends Pipeline<MotionSensorValue, String> {

    private final WindowProducer windowProducer;
    private final FeatureProducer featureProducer;
    private final ClassifyProducer classifyProducer;

    public static int WINDOW_LENGTH_IN_MS = 5 * 1000;

    public HarPipeline(int interval){
        // Window
        windowProducer = new WindowProducer(WINDOW_LENGTH_IN_MS, WINDOW_LENGTH_IN_MS - interval);

        // Feature
        featureProducer = new FeatureProducer();
        windowProducer.setConsumer(featureProducer);

        // Classify
        classifyProducer = new ClassifyProducer();
        featureProducer.setConsumer(classifyProducer);
    }

    public void push(MotionSensorValue message) {
        windowProducer.push(message);
    }

    @Override
    public void setConsumer(Consumer<String> c) {
        classifyProducer.setConsumer(c);
    }
}
