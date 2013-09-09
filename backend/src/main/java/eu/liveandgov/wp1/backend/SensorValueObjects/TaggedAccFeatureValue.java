package eu.liveandgov.wp1.backend.SensorValueObjects;

public class TaggedAccFeatureValue extends AccFeatureValue {
	public String tag = "none";
	
	public TaggedAccFeatureValue(AccFeatureValue t) {
		super(t);
	}

	public static TaggedAccFeatureValue fromWindow (AccSampleWindow window, String tag) {		
		AccFeatureValue f = AccFeatureValue.fromWindow(window);
		TaggedAccFeatureValue t = new TaggedAccFeatureValue(f);
		t.tag = tag;
		return t;
	}
	
	public String toString() {
		return String.format("TAFV - t:%s ts:%d xMean:%f ", tag, startTime, xMean);
	}
	
	public String toCSV(){
		return String.format("%f,%f,%f,%f,%f,%s", S2Mean, S2Sd, xMean, yMean, zMean, tag );
	}
	
	public Object[] toWekaObjArr() {
		Object r[] = new Object[5];
		r[0] = (double)S2Mean;
		r[1] = (double)S2Sd;
		r[2] = (double)xMean;
		r[3] = (double)yMean;
		r[4] = (double)zMean;
		return r;
	}
}
