package eu.liveandgov.sensorcollectorv3.SensorValueObjects;


import android.hardware.SensorEvent;

public class GPSSensorValue extends SensorValue {

	public final SampleType type = SampleType.GPS;
	public double latitude;
	public double longitude;
	public double altitude;

    public static GPSSensorValue fromSensorEvent(SensorEvent event, String id){
        GPSSensorValue out = new GPSSensorValue();

        out.timestamp = event.timestamp;
        out.id = id;

        out.latitude= event.values[0];
        out.latitude = event.values[1];
        out.altitude = event.values[2];

        return out;
    }

    public static GPSSensorValue fromRSV(RawSensorValue rsv) {
		GPSSensorValue out = new GPSSensorValue();

		out.timestamp = rsv.timestamp;
		out.id = rsv.id;

		String[] stringValues = rsv.value.split(" ");
		if (stringValues.length != 3)
			throw new IllegalArgumentException("Cannot parse value "
					+ rsv.value);

		out.longitude = Double.parseDouble(stringValues[0]);
		out.latitude = Double.parseDouble(stringValues[1]);
		out.altitude = Double.parseDouble(stringValues[2]);

		return out;
	}

    @Override
	public String toString() {
		return String.format("GSV - type:%s ts:%d id:%s lon:%f lat:%f alt:%f", type,
				timestamp, id, longitude, latitude, altitude);
	}
}
