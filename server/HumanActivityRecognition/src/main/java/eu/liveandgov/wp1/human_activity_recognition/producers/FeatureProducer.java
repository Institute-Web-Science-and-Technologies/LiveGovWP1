package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureProducer extends Producer<FeatureVector> implements Consumer<TaggedWindow> {

    public void push(TaggedWindow m) {
        FeatureVector vec = new FeatureVector(m);
        consumer.push(vec);
    }
}