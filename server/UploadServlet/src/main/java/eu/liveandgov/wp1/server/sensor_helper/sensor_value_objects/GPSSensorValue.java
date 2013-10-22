package eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects;

import eu.liveandgov.wp1.server.sensor_helper.SampleType;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;

public class GPSSensorValue extends AbstractSensorValue implements SensorValueInterface {
	public final double latitude;
	public final double longitude;
	public final double altitude;

    public GPSSensorValue(long timestamp, String id, String value) {
        super(timestamp, id);

        String[] stringValues = value.split(" ");
        if (stringValues.length != 3) throw new IllegalArgumentException("Cannot parse value " + value);

        this.latitude  = Double.parseDouble(stringValues[0]);
        this.longitude = Double.parseDouble(stringValues[1]);
        this.altitude  = Double.parseDouble(stringValues[2]);
    }

	public String toSSF() {
		return String.format("GPS,%d,%s,%f %f %f", timestamp, id, latitude, longitude, altitude);
	}

    public SampleType getType(){
        return SampleType.GPS;
    }

}
