package eu.liveandgov.wp1.backend.SensorValueObjects;

public class AccSensorValue extends SensorValue {
	public final String type = "ACC";
	public float x;
	public float y;
	public float z;
	
	public static AccSensorValue parse(String s){
		AccSensorValue o = new AccSensorValue();
		
		String[] stringValues = s.split(" ");
		if (stringValues.length < 3) {
			System.out.println("Warning: Cannot parse line " + s);
			return null;
		}
		
		o.x = Float.parseFloat(stringValues[0]);
		o.y = Float.parseFloat(stringValues[1]);
		o.z = Float.parseFloat(stringValues[2]);
		
		return o;
	}
		
}
