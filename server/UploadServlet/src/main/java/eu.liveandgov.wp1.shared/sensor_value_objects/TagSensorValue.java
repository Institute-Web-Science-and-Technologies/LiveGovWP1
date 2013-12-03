package eu.liveandgov.wp1.shared.sensor_value_objects;

import eu.liveandgov.wp1.shared.SampleType;

public class TagSensorValue extends AbstractSensorValue implements SensorValueInterface {
	public String tag;

    public TagSensorValue(long timestamp, String id, String value) {
        super(timestamp, id);
        tag = value;
    }

    public String toSSF() {
        return String.format("TAG,%d,%s,%s",timestamp, id, tag);
    }

    public SampleType getType(){
        return SampleType.TAG;
    }

    public String getTag(){
        return tag;
    }

}
