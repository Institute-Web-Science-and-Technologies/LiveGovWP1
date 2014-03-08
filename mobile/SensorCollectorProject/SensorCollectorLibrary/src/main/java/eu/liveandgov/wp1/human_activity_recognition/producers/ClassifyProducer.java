package eu.liveandgov.wp1.human_activity_recognition.producers;

import android.util.Log;

import eu.liveandgov.wp1.human_activity_recognition.classifier.ManualClassify;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector;

/**
 * Created by cehlen on 10/19/13.
 */
public class ClassifyProducer extends Producer<String> implements Consumer<FeatureVector> {

    public void push(FeatureVector m) {
        try {
            String activity = ManualClassify.classify(m);
            Log.v("ACT", activity);
            consumer.push(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
