package eu.liveandgov.wp1.backend.SensorValueObjects;

import java.util.List;

public class AccFeatureValue {
	public long startTime;
	public float xMean;
	public float variance;
	
	public static AccFeatureValue fromWindow(SampleWindow<AccSensorValue> window) {
		AccFeatureValue o = new AccFeatureValue();
		o.startTime = window.getStartTime();

		List<AccSensorValue> values =  window.getValues();
		
		o.xMean = 0;
		for(int i=0; i < values.size(); i++)	o.xMean += ((AccSensorValue) values.get(i)).x;
		o.xMean = o.xMean / values.size();
		
		o.variance = 0;
		for(int i=0; i < values.size(); i++)	o.variance += Math.pow((((AccSensorValue) values.get(i)).x - o.xMean),2);
		o.variance = o.variance / values.size();
		
		return o;
	}
	
	public String toString(){
		return String.format("AFV - ts:%d xMean:%f var:%f", startTime, xMean, variance);
	}
}
