package eu.liveandgov.sensorcollectorv3.har;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.Producer;
import eu.liveandgov.sensorcollectorv3.sensors.Window;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureProducer extends Producer<FeatureVector> implements Consumer<Window> {

    @Override
    public void push(Window m) {
        FeatureVector vec = new FeatureVector();

        vec.xMean = FeatureHelper.mean(m.x);
        vec.xVar = FeatureHelper.var(m.x);
        vec.yMean = FeatureHelper.mean(m.y);
        vec.yVar = FeatureHelper.var(m.y);
        vec.yMean = FeatureHelper.mean(m.z);
        vec.yVar = FeatureHelper.var(m.z);

        float[] S2 = FeatureHelper.S2(m.x, m.y, m.z);
        vec.s2Mean = FeatureHelper.mean(S2);
        vec.s2Var = FeatureHelper.var(S2);

        consumer.push(vec);
    }
}
