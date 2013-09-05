package eu.liveandgov.wp1.backend.SensorValueObjects;

import eu.liveandgov.wp1.backend.format.SampleType;

public class AccSensorValue extends SensorValue {
	public final SampleType type = SampleType.ACC;
	public float x;
	public float y;
	public float z;
	
	public static AccSensorValue fromRSV(RawSensorValue rsv){
		AccSensorValue out = new AccSensorValue();
		
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
		return String.format("ASV - type:%s ts:%d id:%s x:%f y:%f z:%f", type, timestamp, id, x,y,z);
	}
}
