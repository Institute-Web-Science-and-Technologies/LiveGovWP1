package eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects;

import eu.liveandgov.wp1.server.sensor_helper.SampleType;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;

public class GoogleActivitySensorValue extends AbstractSensorValue implements SensorValueInterface {
	public final String activity;

    public GoogleActivitySensorValue(long timestamp, String id, String value) {
        super(timestamp, id);
        activity = value;
    }

    public String toSSF() {
        return String.format("ACT,%d,%s,%s",timestamp,id,activity);
    }

    public SampleType getType(){
        return SampleType.ACT;
    }

}
