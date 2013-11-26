package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.connectors.Consumer;
import eu.liveandgov.wp1.connectors.Producer;
import eu.liveandgov.wp1.sensors.TaggedWindow;

/**
 * Created by cehlen on 10/19/13.
 */
public class TaggedFeatureProducer extends Producer<TaggedFeatureVector> implements Consumer<TaggedWindow> {

    @Override
    public void push(TaggedWindow m) {
        TaggedFeatureVector vec = new TaggedFeatureVector(m);

        consumer.push(vec);
    }
}