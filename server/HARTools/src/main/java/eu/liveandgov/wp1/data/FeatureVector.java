package eu.liveandgov.wp1.data;

import eu.liveandgov.wp1.data.Window;
import eu.liveandgov.wp1.helper.BinDistributor;
import eu.liveandgov.wp1.helper.FeatureHelper;
import org.apache.commons.lang.StringUtils;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureVector {

    private final int S2_BINS_NUM = 10;
    private final int S2_FT_BINS_NUM = 5;

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

    public long startTime = 0;

    // TODO: FFT Features

    // TODO: Historgram Features

    public FeatureVector(){}


    public FeatureVector(Window m) {

        xMean = FeatureHelper.mean(m.x);
        xVar = FeatureHelper.var(m.x);
        yMean = FeatureHelper.mean(m.y);
        yVar = FeatureHelper.var(m.y);
        zMean = FeatureHelper.mean(m.z);
        zVar = FeatureHelper.var(m.z);

        float[] S2 = FeatureHelper.S2(m.x, m.y, m.z);
        s2Mean = FeatureHelper.mean(S2);
        s2Var = FeatureHelper.var(S2);

        tilt = FeatureHelper.tilt(xMean, yMean, zMean);
        energy = FeatureHelper.sum(S2);
        kurtosis = FeatureHelper.kurtosis(S2);

        BinDistributor BD = new BinDistributor(0,20,S2_BINS_NUM);
        S2Bins = BD.getBinsForAxis(S2);

        BinDistributor FBD = new BinDistributor(0,100,S2_FT_BINS_NUM);
        S2FTBins = FBD.getBinsForAxis(FeatureHelper.FTAbsolute(S2));

        startTime = m.startTime;
    }


    public String toCSV() {
        String out = String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",
                id, tag, xMean, yMean, zMean, xVar, yVar, zVar, s2Mean, s2Var, tilt, energy, kurtosis
        );

//        out += "," + StringUtils.join(S2Bins, ',');
        out += "," + StringUtils.join(S2FTBins, ',');

        return out;
    }

    public String toString() {
        return toCSV();
    }


    // Needed for classification
    public Object[] toWekaObjArr() {
        int size = 11 + S2_FT_BINS_NUM + 2;
        Object r[] = new Object[size];
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
//        for(int i = 0; i < S2_BINS_NUM+2; i++) {
//            r[i+10] = (double)S2Bins[i];
//        }
        for(int i = 0; i < S2_FT_BINS_NUM+2; i++) {
            r[i+S2_BINS_NUM] = (double)S2FTBins[i];
        }

        return r;

    }

}