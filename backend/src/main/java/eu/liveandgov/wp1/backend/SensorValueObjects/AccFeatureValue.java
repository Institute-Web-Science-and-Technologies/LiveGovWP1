package eu.liveandgov.wp1.backend.SensorValueObjects;


public class AccFeatureValue {
	public long startTime;
	public float xMean;
	public float variance;
	
	public static AccFeatureValue fromWindow(SampleWindow window) {
		AccFeatureValue o = new AccFeatureValue();
		o.startTime = window.startTime;

		o.xMean = 0;
		for(int i=0; i < window.values.length; i++)	o.xMean += ((AccSensorValue) window.values[i]).x;
		o.xMean = o.xMean / window.values.length;
		
		o.variance = 0;
		for(int i=0; i < window.values.length; i++)	o.variance += Math.pow((((AccSensorValue) window.values[i]).x - o.xMean),2);
		o.variance = o.variance / window.values.length;
		
		return o;
	}
}
