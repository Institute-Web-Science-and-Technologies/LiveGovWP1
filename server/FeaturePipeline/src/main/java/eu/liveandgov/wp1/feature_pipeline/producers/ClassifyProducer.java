package eu.liveandgov.wp1.feature_pipeline.producers;

import eu.liveandgov.wp1.feature_pipeline.classifier.DecisionTree;
import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Producer;
import eu.liveandgov.wp1.feature_pipeline.containers.FeatureVector;

/**
 * Created by cehlen on 10/19/13.
 */
public class ClassifyProducer extends Producer<String> implements Consumer<FeatureVector> {

    public void push(FeatureVector m) {
        try {
            String activity = DecisionTree.myClassify(m.toWekaObjArr());
            consumer.push(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
