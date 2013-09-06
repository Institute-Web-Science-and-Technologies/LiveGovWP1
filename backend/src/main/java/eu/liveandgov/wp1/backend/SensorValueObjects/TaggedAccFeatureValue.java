package eu.liveandgov.wp1.backend.SensorValueObjects;

public class TaggedAccFeatureValue extends AccFeatureValue {
	public String tag = "none";
	
	public TaggedAccFeatureValue(AccFeatureValue t) {
		super(t);
	}

	public static TaggedAccFeatureValue fromWindow (SampleWindow<AccSensorValue> window, String tag) {		
		AccFeatureValue f = AccFeatureValue.fromWindow(window);
		TaggedAccFeatureValue t = new TaggedAccFeatureValue(f);
		t.tag = tag;
		return t;
	}
	
	public String toString() {
		return String.format("TAFV - t:%s ts:%d xMean:%f var:%f", tag, startTime, xMean, variance);
	}
	
	public String toCSV(){
		return String.format("%s,%f,%f,%f,%f,%f", tag, S2Mean, S2Sd, xMean, yMean, zMean );
	}
}
