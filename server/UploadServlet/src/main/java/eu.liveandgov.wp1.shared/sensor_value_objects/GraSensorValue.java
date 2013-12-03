package eu.liveandgov.wp1.shared.sensor_value_objects;

import eu.liveandgov.wp1.shared.SampleType;

public class GraSensorValue extends AbstractMotionSensorValue implements SensorValueInterface {

    public GraSensorValue(long timestamp, String id, float x, float y, float z) {
        super(timestamp, id, x, y, z);
    }

    public String toSSF() {
        return "GRA," + super.baseSSF();
    }

    public SampleType getType(){
        return SampleType.GRA;
    }

}
