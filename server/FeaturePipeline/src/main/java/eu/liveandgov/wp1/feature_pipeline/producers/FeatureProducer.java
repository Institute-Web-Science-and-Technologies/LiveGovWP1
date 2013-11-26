package eu.liveandgov.wp1.feature_pipeline.producers;

import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Producer;
import eu.liveandgov.wp1.feature_pipeline.containers.FeatureVector;
import eu.liveandgov.wp1.feature_pipeline.containers.TaggedWindow;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureProducer extends Producer<FeatureVector> implements Consumer<TaggedWindow> {

    public void push(TaggedWindow m) {
        FeatureVector vec = new FeatureVector(m);

        consumer.push(vec);
    }
}