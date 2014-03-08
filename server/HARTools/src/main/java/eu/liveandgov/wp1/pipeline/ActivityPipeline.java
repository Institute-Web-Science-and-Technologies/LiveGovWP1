package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.classifier.ManualClassify;
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
                    String activity = ManualClassify.classify(longFeatureVectorTuple.right);

                    Triple<Long, Long, String> t = new Triple<Long, Long, String>(longFeatureVectorTuple.left,
                            longFeatureVectorTuple.right.startTime,
                            activity);

                    produce(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
