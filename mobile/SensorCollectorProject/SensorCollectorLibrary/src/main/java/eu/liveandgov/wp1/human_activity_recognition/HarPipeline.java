package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.human_activity_recognition.producers.ClassifyProducer;
import eu.liveandgov.wp1.human_activity_recognition.producers.FeatureProducer;
import eu.liveandgov.wp1.human_activity_recognition.producers.SmoothingProducer;
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
    private final SmoothingProducer smoothingProducer;

    public static int WINDOW_LENGTH_IN_MS = 2 * 1000;
    public static int INTERVAL = 500;

    public HarPipeline(){

        // Window
        windowProducer = new WindowProducer(WINDOW_LENGTH_IN_MS, WINDOW_LENGTH_IN_MS - INTERVAL);

        // Feature
        featureProducer = new FeatureProducer();
        windowProducer.setConsumer(featureProducer);

        // Classify
        classifyProducer = new ClassifyProducer();
        featureProducer.setConsumer(classifyProducer);

        smoothingProducer = new SmoothingProducer(5);
        classifyProducer.setConsumer(smoothingProducer);
    }

    public void push(MotionSensorValue message) {
        windowProducer.push(message);
    }

    @Override
    public void setConsumer(Consumer<String> c) {
        smoothingProducer.setConsumer(c);
    }
}
