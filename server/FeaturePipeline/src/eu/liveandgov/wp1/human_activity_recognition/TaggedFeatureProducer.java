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
        TaggedFeatureVector vec = new TaggedFeatureVector();

        vec.tag = m.tag;

        vec.xMean = FeatureHelper.mean(m.x);
        vec.xVar = FeatureHelper.var(m.x);
        vec.yMean = FeatureHelper.mean(m.y);
        vec.yVar = FeatureHelper.var(m.y);
        vec.zMean = FeatureHelper.mean(m.z);
        vec.zVar = FeatureHelper.var(m.z);

        float[] S2 = FeatureHelper.S2(m.x, m.y, m.z);
        vec.s2Mean = FeatureHelper.mean(S2);
        vec.s2Var = FeatureHelper.var(S2);

        consumer.push(vec);
    }
}