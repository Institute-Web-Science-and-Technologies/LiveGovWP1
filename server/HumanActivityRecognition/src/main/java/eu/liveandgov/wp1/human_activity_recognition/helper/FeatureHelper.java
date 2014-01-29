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
            out[i] = (float)Math.sqrt(x[i]*x[i] + y[i]*y[i] + z[i]*z[i]);
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
        double abs = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(x,2));
        return (float) (Math.abs(y) / abs);
    }

    public static float [] padZero(float[] input) {
        if (input == null) { return null; }
        if (input.length == 0) { return new float [0]; }
        int nextPowerOf2 = (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(input.length - 1));

        float [] output = new float[nextPowerOf2];
        for (int i = 0; i < nextPowerOf2; i++){
            if (i < input.length) {
                output[i] = input[i];
            } else {
                output[i] = 0;
            }
        }
        return output;
    }

    public static Complex[] FT(float[] input){
        return FFT(padZero(input));
    }

    public static Complex[] FFT(float[] input){
        double[] dinput = new double[input.length];
        for (int i = 0; i < input.length; i++){
            dinput[i] = (double) input[i];
        }

        FastFourierTransformer fT = new FastFourierTransformer(DftNormalization.STANDARD);
        return fT.transform(dinput, TransformType.FORWARD);
    }

    public static float[] Abs(Complex [] input){
        float [] output = new float [input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i].abs();
        }
        return output;
    }

    public static float[] FTAbsolute(float[] input){
        return Abs(FT(input));
    }


}
