package eu.liveandgov.wp1.human_activity_recognition.containers;

import eu.liveandgov.wp1.human_activity_recognition.helper.BinDistributor;
import eu.liveandgov.wp1.human_activity_recognition.helper.FeatureHelper;
import org.apache.commons.lang.StringUtils;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureVector_FFT {

    public String tag = "";
    public String id = "";

    // STATISTICAL FEATURES
    public float xMean = 0F;
    public float yMean = 0F;
    public float zMean = 0F;

    public float xVar = 0F;
    public float yVar = 0F;
    public float zVar = 0F;

    public float s2Mean = 0F;
    public float s2Var = 0F;

    // Tilting Angle
    public float tilt = 0F;
    public float energy = 0F;
    public float kurtosis = 0F;

    public Integer [] S2Bins = new Integer[0];
    public Integer [] S2FTBins = new Integer[0];

    // TODO: FFT Features

    // TODO: Historgram Features

    public FeatureVector_FFT(){}


    public FeatureVector_FFT(CountWindow m) {
        tag = m.tag;
        id = m.id;

        xMean = FeatureHelper.mean(m.x);
        xVar = FeatureHelper.var(m.x);
        yMean = FeatureHelper.mean(m.y);
        yVar = FeatureHelper.var(m.y);
        zMean = FeatureHelper.mean(m.z);
        zVar = FeatureHelper.var(m.z);

        float[] S2 = FeatureHelper.S2(m.x, m.y, m.z);
        s2Mean = FeatureHelper.mean(S2);
        s2Var = FeatureHelper.var(S2);

        tilt = FeatureHelper.tilt(xMean,yMean,zMean);
        energy = FeatureHelper.sum(S2);
        kurtosis = FeatureHelper.kurtosis(S2);

        BinDistributor BD = new BinDistributor(0,20,10);
        S2Bins = BD.getBinsForAxis(S2);

        BinDistributor FBD = new BinDistributor(0,100,10);
        S2FTBins = FBD.getBinsForAxis(FeatureHelper.FTAbsolute(S2));
    }


    public String toCSV() {
        String out = String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",
            id, tag, xMean, yMean, zMean, xVar, yVar, zVar, s2Mean, s2Var, tilt, energy, kurtosis
        );

        out += "," + StringUtils.join(S2Bins, ',');
        out += "," + StringUtils.join(S2FTBins, ',');

        return out;
    }

    public String toString() {
        return toCSV();
    }


    // Needed for classification
    public Object[] toWekaObjArr() {
        Object r[] = new Object[11];
        r[0] = (double)xMean;
        // Currently we are using the SD because our first tree was using it
        r[1] = (double)yMean; // THIS NEEDS TO BE SQRT FOR THE CRAPPY CLASSIFIER
        r[2] = (double)zMean;
        r[3] = (double)xVar;
        r[4] = (double)yVar;
        r[5] = (double)zVar;
        r[6] = (double)s2Mean;
        r[7] = (double)s2Var;
        r[8] = (double)tilt;
        r[9] = (double)energy;
        r[10] = (double)kurtosis;

        return r;

    }

}
