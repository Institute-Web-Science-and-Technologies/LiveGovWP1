package eu.liveandgov.wp1.feature_pipeline.producers;

import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Producer;
import eu.liveandgov.wp1.feature_pipeline.containers.TaggedFeatureVector;
import eu.liveandgov.wp1.feature_pipeline.containers.TaggedWindow;

/**
 * Created by cehlen on 10/19/13.
 */
public class TaggedFeatureProducer extends Producer<TaggedFeatureVector> implements Consumer<TaggedWindow> {

    public void push(TaggedWindow m) {
        TaggedFeatureVector vec = new TaggedFeatureVector(m);

        consumer.push(vec);
    }
}