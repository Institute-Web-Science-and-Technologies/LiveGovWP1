package eu.liveandgov.wp1.server.SensorValueObjects;

public class LacSensorValue extends SensorValue {
	public final SampleType type = SampleType.LAC;
	public float x;
	public float y;
	public float z;
	
	public static LacSensorValue fromRSV(RawSensorValue rsv){
		LacSensorValue out = new LacSensorValue();
		
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
		return String.format("LASV - type:%s ts:%d id:%s x:%f y:%f z:%f", type, timestamp, id, x,y,z);
	}
}
