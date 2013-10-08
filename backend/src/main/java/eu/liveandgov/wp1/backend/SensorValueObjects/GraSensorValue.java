package eu.liveandgov.wp1.backend.SensorValueObjects;

import eu.liveandgov.wp1.backend.format.SampleType;

public class GraSensorValue extends SensorValue {
	public final SampleType type = SampleType.GRA;
	public float x;
	public float y;
	public float z;
	
	public static GraSensorValue fromRSV(RawSensorValue rsv){
		GraSensorValue out = new GraSensorValue();
		
		out.timestamp = rsv.timestamp;
		out.id = rsv.id;

		String[] stringValues = rsv.value.split(" ");
		if (stringValues.length != 3) throw new IllegalArgumentException("Cannot parse value " + rsv.value);
		
		out.x = Float.parseFloat(stringValues[0]);
		out.y = Float.parseFloat(stringValues[1]);
		out.z = Float.parseFloat(stringValues[2]);
		
		return out;
	}
	
	public String toString(){
		return String.format("GRSV - type:%s ts:%d id:%s x:%f y:%f z:%f", type, timestamp, id, x,y,z);
	}
}
