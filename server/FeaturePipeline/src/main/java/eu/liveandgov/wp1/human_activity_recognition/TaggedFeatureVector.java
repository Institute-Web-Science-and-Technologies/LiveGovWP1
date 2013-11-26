package eu.liveandgov.wp1.human_activity_recognition;

import eu.liveandgov.wp1.sensors.TaggedWindow;

/**
 * Created by cehlen on 10/19/13.
 */
public class TaggedFeatureVector {


    public String tag;

    // STATISTICAL FEATURES
    public float xMean;
    public float yMean;
    public float zMean;

    public float xVar;
    public float yVar;
    public float zVar;

    public float s2Mean;
    public float s2Var;

    // Tilting Angle
    public float tilt;
    public float energy;
    public float kurtosis;

    // FFT Features

    public TaggedFeatureVector(TaggedWindow m) {
        tag = m.tag;

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

    }


    public static String getCsvHead() {
        return "xMean,yMean,zMean,xVar,yVar,zVar,s2Mean,s2Var,tilt,energy,kurtosis,tag";
    }

    public String toCSV() {
        return String.format("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%s",
            xMean, yMean, zMean, xVar, yVar, zVar, s2Mean, s2Var, tilt, energy, kurtosis, tag
        );
    }
}
