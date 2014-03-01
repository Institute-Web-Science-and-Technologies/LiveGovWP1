package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.FeatureVector;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.Window;

/**
 * Created by cehlen on 25/02/14.
 */
public class FeaturePipeline extends Pipeline<Tuple<Long, Window>, Tuple<Long, FeatureVector>> {
    @Override
    public void push(Tuple<Long, Window> longWindowTuple) {
        FeatureVector vec = new FeatureVector(longWindowTuple.right);
        Tuple<Long, FeatureVector> t = new Tuple<Long, FeatureVector>(longWindowTuple.left, vec);
        produce(t);
    }
}
