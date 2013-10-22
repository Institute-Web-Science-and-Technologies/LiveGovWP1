package eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects;

import eu.liveandgov.wp1.server.sensor_helper.SampleType;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;

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
