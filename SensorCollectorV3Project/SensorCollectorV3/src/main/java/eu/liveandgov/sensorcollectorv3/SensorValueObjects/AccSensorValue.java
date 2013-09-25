package eu.liveandgov.sensorcollectorv3.SensorValueObjects;


import android.hardware.SensorEvent;

public class AccSensorValue extends SensorValue {
	public final SampleType type = SampleType.ACC;
	public float x;
	public float y;
	public float z;

    public static AccSensorValue fromSensorEvent(SensorEvent event, String id){
        AccSensorValue out = new AccSensorValue();

        out.timestamp = event.timestamp;
        out.id = id;

        out.x = event.values[0];
        out.x = event.values[1];
        out.x = event.values[2];

        return out;
    }

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

    @Override
	public String toString(){
        return String.format("ACC - type:%s ts:%d id:%s x:%f y:%f z:%f", type, timestamp, id, x,y,z);
	}
}
