package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.classifier.DecisionTree;
import eu.liveandgov.wp1.human_activity_recognition.classifier.GoodTree;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector;

/**
 * Created by cehlen on 10/19/13.
 */
public class ClassifyProducer extends Producer<String> implements Consumer<FeatureVector> {

    public void push(FeatureVector m) {
        try {
            String activity = GoodTree.myClassify(m.toWekaObjArr());
            consumer.push(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
