package eu.liveandgov.sensorcollectorv3.human_activity_recognition;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureVector {

    public float xMean;
    public float yMean;
    public float zMean;

    public float xVar;
    public float yVar;
    public float zVar;

    public float s2Mean;
    public float s2Var;

    public Object[] toWekaObjArr() {
        Object r[] = new Object[8];
        r[0] = (double)xMean;
        // Currently we are using the SD because our first tree was using it
        r[1] = (double)yMean; // THIS NEEDS TO BE SQRT FOR THE CRAPPY CLASSIFIER
        r[2] = (double)zMean;
        r[3] = (double)xVar;
        r[4] = (double)yVar;
        r[5] = (double)zVar;
        r[6] = (double)s2Mean;
        r[7] = (double)s2Var;

        return r;

    }

}
