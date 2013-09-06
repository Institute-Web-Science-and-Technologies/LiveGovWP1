package eu.liveandgov.wp1.backend.SensorValueObjects;

public class TaggedAccFeatureValue extends AccFeatureValue {
	public String tag = "none";
	
	public static TaggedAccFeatureValue fromWindow (SampleWindow<AccSensorValue> window, String tag) {
		AccFeatureValue f = AccFeatureValue.fromWindow(window);
		TaggedAccFeatureValue t = new TaggedAccFeatureValue();
		t.startTime = f.startTime;
		t.variance = f.variance;
		t.xMean = f.xMean;
		t.tag = tag;
		return t;
	}
	
	public String toString() {
		return String.format("TAFV - t:%s ts:%d xMean:%f var:%f", tag, startTime, xMean, variance);
	}
}
