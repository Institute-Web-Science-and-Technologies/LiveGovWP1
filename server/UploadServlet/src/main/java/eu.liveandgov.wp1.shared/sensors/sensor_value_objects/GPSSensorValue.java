package eu.liveandgov.wp1.shared.sensors.sensor_value_objects;

import eu.liveandgov.wp1.shared.sensors.SampleType;

public class GPSSensorValue extends AbstractSensorValue implements SensorValueInterface {
	public final double latitude;
	public final double longitude;
	public final double altitude;

    public GPSSensorValue(long timestamp, String id, String value) {
        super(timestamp, id);

        String[] stringValues = value.split(" ");
        if (stringValues.length == 3) {
            this.latitude  = Double.parseDouble(stringValues[0]);
            this.longitude = Double.parseDouble(stringValues[1]);
            this.altitude  = Double.parseDouble(stringValues[2]);
        } else if (stringValues.length == 2){
            this.latitude  = Double.parseDouble(stringValues[0]);
            this.longitude = Double.parseDouble(stringValues[1]);
            this.altitude  = new Double(0);
        } else {
            throw new IllegalArgumentException("Cannot parse value " + value);
        }
    }

	public String toSSF() {
		return String.format("GPS,%d,%s,%f %f %f", timestamp, id, latitude, longitude, altitude);
	}

    public SampleType getType(){
        return SampleType.GPS;
    }

}
