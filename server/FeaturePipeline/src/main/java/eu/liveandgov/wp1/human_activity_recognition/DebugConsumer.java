package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.connectors.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 12/11/13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class DebugConsumer implements Consumer<TaggedFeatureVector> {

    @Override
    public void push(TaggedFeatureVector fv) {
        System.out.println(String.format("Xmean: %f | Ymean: %f | Zmean: %f | Xvar: %f | Yvar: %f | Zvar: %f | s2Mean: %f | s2Var: %f",
                fv.xMean, fv.yMean, fv.zMean, fv.xVar, fv.yVar, fv.zVar, fv.s2Mean, fv.s2Var));
    }
}
