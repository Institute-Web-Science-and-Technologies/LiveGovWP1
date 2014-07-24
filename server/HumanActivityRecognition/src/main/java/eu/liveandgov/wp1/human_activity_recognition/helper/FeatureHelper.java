package eu.liveandgov.wp1.human_activity_recognition.helper;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 10/15/13
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FeatureHelper {

    private static DescriptiveStatistics stats = new DescriptiveStatistics();


    public static float mean(float[] input) {
        stats.clear();
        for(float v: input) {
            stats.addValue(v);
        }
        return (float)stats.getMean();
    }

    public static float var(float[] input) {
        stats.clear();
        for(float v: input) {
            stats.addValue(v);
        }
        return (float)stats.getVariance();
    }

    public static float kurtosis(float[] input) {
        stats.clear();
        for(float v: input) {
            stats.addValue(v);
        }
        return (float)stats.getKurtosis();
    }

    public static float[] S2(float[] x, float[] y, float[] z){
        float[] out = new float[x.length];
        for (int i =0; i< x.length; i++){
            out[i] = x[i]*x[i] + y[i]*y[i] + z[i]*z[i];
        }
        return out;
    }

    public static float sum(float[] xv) {
        float sum = 0;
        for (float x: xv){
            sum += x;
        }
        return sum;
    }

    // Tilting measure to ground:
    // 1 if Mobile is pointing to sky or ground
    // 0 if lying flat
    // arccos of return value gives tilting angle
    public static float tilt(float x,float y,float z){
        double abs = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
        return (float) (Math.abs(y) / abs);
    }

    public static Complex[] FFT(float[] input){
        double[] dinput = new double[input.length];
        for (int i = 0; i < input.length; i++){
            dinput[i] = (double) input[i];
        }

        FastFourierTransformer fT = new FastFourierTransformer(DftNormalization.STANDARD);
        return fT.transform(dinput, TransformType.FORWARD);
    }

}
