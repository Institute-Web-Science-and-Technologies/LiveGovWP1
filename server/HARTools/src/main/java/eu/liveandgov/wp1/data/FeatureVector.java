package eu.liveandgov.wp1.data;

import eu.liveandgov.wp1.helper.BinDistributor;
import eu.liveandgov.wp1.helper.FeatureHelper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureVector {

    private static final int FFT_BIN_CNT = 5;
    private static final int BIN_CNT = 10;

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

    // public float energy = 0F;
    // public float kurtosis = 0F;

    // public int [] S2Bins = new Integer[0];
    public int [] S2FTBins = new int[0];

    public long startTime = 0;

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

        tilt = FeatureHelper.tilt(xMean,yMean,zMean);
        // tilt = FeatureHelper.tiltArray(m.x,m.y,m.z);

        // energy = FeatureHelper.sum(S2); this is the same as s2Mean
        // kurtosis = FeatureHelper.kurtosis(S2);

        // BinDistributor BD = new BinDistributor(0,20,BIN_CNT);
        // S2Bins = BD.getBinsForAxis(S2);

        BinDistributor FBD = new BinDistributor(0,100, FFT_BIN_CNT);
        S2FTBins = FBD.getBinsForAxis(FeatureHelper.FTAbsolute(S2));

        S2FTBins[0] = FeatureHelper.MaxIndex(S2FTBins);

        startTime = m.startTime;
    }

    public static String getCsvHead() {
        StringBuilder out = new StringBuilder("id, tag, xMean, yMean, zMean, xVar, yVar, zVar, s2Mean, s2Var, tilt,");
        out.append("FT_MIN, ");
        for (int i = 0; i < FFT_BIN_CNT; i++){
            out.append("FT_" + i + ", ");
        }
        out.append("FT_MAX");
        return out.toString();
    }

    public String toCSV() {
        String out = String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f",
                id, tag, xMean, yMean, zMean, xVar, yVar, zVar, s2Mean, s2Var, tilt
                // ,energy
                // ,kurtosis
        );

//        out += "," + StringUtils.join(ArrayUtils.toObject(S2Bins), ',');

        out += "," + StringUtils.join(ArrayUtils.toObject(S2FTBins), ',');

        return out;
    }

    public String toString() {
        return toCSV();
    }


    // Needed for classification
    public Object[] toWekaObjArr() {

        Deque<Object> R = new ArrayDeque<Object>();

        // for tag and ID
        R.addLast((double) 0);
        R.addLast((double) 0);

        R.addLast((double)xMean);
        R.addLast((double)yMean);
        R.addLast((double)zMean);
        R.addLast((double)xVar);
        R.addLast((double)yVar);
        R.addLast((double)zVar);
        R.addLast((double)s2Mean);
        R.addLast((double)s2Var);
        R.addLast((double)tilt);

//        R.addLast((double)energy);
//        R.addLast((double)kurtosis);

//        for (float f : S2Bins){
//            R.addLast((double) f);
//        }

        for (float f : S2FTBins){
            R.addLast((double) f);
        }

        return R.toArray();
    }

}