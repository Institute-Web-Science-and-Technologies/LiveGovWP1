package eu.liveandgov.wp1.data;

import eu.liveandgov.wp1.helper.BinDistributor;
import eu.liveandgov.wp1.helper.FeatureHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureVector {

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
    public float roll = 0F;
    // public float energy = 0F;
    public float kurtosis = 0F;

    public Integer[] S2FTBins = new Integer[0];

    public long startTime = 0;

    // TODO: FFT Features

    // TODO: Historgram Features

    public FeatureVector() {
    }


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

        //System.out.println("FV: " + xMean + ", " + yMean + ", " + zMean);
        tilt = FeatureHelper.tilt(xMean, yMean, zMean);
        roll = FeatureHelper.roll(xMean, yMean, zMean);
        // energy = FeatureHelper.sum(S2); this is the same as s2Mean
        kurtosis = FeatureHelper.kurtosis(S2);

        // BinDistributor BD = new BinDistributor(0,20,10);
        // S2Bins = BD.getBinsForAxis(S2);

        BinDistributor FBD = new BinDistributor(0, 100, 5);
        S2FTBins = FBD.getBinsForAxis(FeatureHelper.FTAbsolute(S2));

        startTime = m.startTime;
    }


    public String toCSV() {
        String out = String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",
                id, tag, xMean, yMean, zMean, xVar, yVar, zVar, s2Mean, s2Var, tilt,
                // energy,
                kurtosis
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

        Deque<Object> R = new ArrayDeque<Object>();

        R.addLast((double) xMean);
        R.addLast((double) yMean);
        R.addLast((double) zMean);
        R.addLast((double) xVar);
        R.addLast((double) yVar);
        R.addLast((double) zVar);
        R.addLast((double) s2Mean);
        R.addLast((double) s2Var);
        R.addLast((double) tilt);
        R.addLast((double) kurtosis);
        for (float f : S2FTBins) {
            R.addLast((double) f);
        }

        return R.toArray();

    }

}