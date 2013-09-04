package eu.liveandgov.wp1.backend.SensorValueObjects;

public class AccFeatureValue {
	public long startTime;
	public float xMean;
	public float variance;
	
	public static AccFeatureValue fromWindow(SampleWindow<AccSensorValue> window) {
		AccFeatureValue o = new AccFeatureValue();
		o.startTime = window.startTime;

		AccSensorValue[] values =  window.getValues();
		
		o.xMean = 0;
		for(int i=0; i < values.length; i++)	o.xMean += ((AccSensorValue) values[i]).x;
		o.xMean = o.xMean / values.length;
		
		o.variance = 0;
		for(int i=0; i < values.length; i++)	o.variance += Math.pow((((AccSensorValue) values[i]).x - o.xMean),2);
		o.variance = o.variance / values.length;
		
		return o;
	}
}
