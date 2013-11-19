package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

import android.util.Log;

import eu.liveandgov.sensorcollectorv3.connectors.Consumer;
import eu.liveandgov.sensorcollectorv3.connectors.Producer;
import eu.liveandgov.sensorcollectorv3.human_activity_recognition.classifier.BetterDecisionTree;
import eu.liveandgov.sensorcollectorv3.human_activity_recognition.classifier.CrappyDecisionTree;

/**
 * Created by cehlen on 10/19/13.
 */
class ClassifyProducer extends Producer<String> implements Consumer<FeatureVector> {

    @Override
    public void push(FeatureVector m) {
        try {
            String activity = BetterDecisionTree.myClassify(m.toWekaObjArr());
            Log.i("HAR_CLASS", activity);
            consumer.push(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
