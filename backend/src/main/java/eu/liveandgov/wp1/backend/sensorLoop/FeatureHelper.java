package eu.liveandgov.wp1.backend.sensorLoop;

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
	
	public static Complex[] FFT(float[] input){
		double[] dinput = new double[input.length];
		for (int i = 0; i < input.length; i++){
			dinput[i] = (double) input[i];
		}
		
		FastFourierTransformer fT = new FastFourierTransformer(DftNormalization.STANDARD);
		return fT.transform(dinput, TransformType.FORWARD);
	}
	
}
