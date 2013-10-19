package eu.liveandgov.sensorcollectorv3.har;

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
        r[0] = (double)s2Mean;
        // Currently we are using the SD because our first tree was using it
        r[1] = Math.sqrt(s2Var);
        r[2] = (double)xMean;
        r[3] = (double)yMean;
        r[4] = (double)zMean;
        r[5] = (double)xVar;
        r[6] = (double)yVar;
        r[7] = (double)zVar;

        return r;

    }

}
