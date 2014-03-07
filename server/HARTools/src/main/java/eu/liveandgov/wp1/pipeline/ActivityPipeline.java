package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.classifier.UKOBClassifier;
import eu.liveandgov.wp1.classifier.UKOB_NEW_Classifier;
import eu.liveandgov.wp1.data.FeatureVector;
import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.data.Tuple;

/**
 * Created by cehlen on 25/02/14.
 */
public class ActivityPipeline extends Pipeline<Tuple<Long, FeatureVector>, Triple<Long, Long, String>>  {
    private int classifier_id = 0;

    public ActivityPipeline(int classifier_id) {
        this.classifier_id = classifier_id;
    }

    @Override
    public void push(Tuple<Long, FeatureVector> longFeatureVectorTuple) {
        switch (this.classifier_id) {
            case 0:
                try {
                    double a = UKOB_NEW_Classifier.classify(longFeatureVectorTuple.right.toWekaObjArr());
                    String activity = UKOB_NEW_Classifier.getActivityName((int)a);
                    Triple<Long, Long, String> t = new Triple<Long, Long, String>(longFeatureVectorTuple.left,
                            longFeatureVectorTuple.right.startTime,
                            activity);

                    produce(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    double a = UKOBClassifier.classify(longFeatureVectorTuple.right.toWekaObjArr());
                    String activity = UKOBClassifier.getActivityName((int)a);


                    Triple<Long, Long, String> t = new Triple<Long, Long, String>(longFeatureVectorTuple.left,
                                                                                  longFeatureVectorTuple.right.startTime,
                                                                                  activity);
                    produce(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
