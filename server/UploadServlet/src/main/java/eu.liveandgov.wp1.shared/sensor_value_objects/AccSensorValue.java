package eu.liveandgov.wp1.shared.sensor_value_objects;

import eu.liveandgov.wp1.shared.SampleType;

public class AccSensorValue extends AbstractMotionSensorValue implements SensorValueInterface {

    public AccSensorValue(long timestamp, String id, float x, float y, float z) {
        super(timestamp, id, x, y, z);
    }

    public String toSSF(){
        return "ACC," + baseSSF();
    }

    public SampleType getType(){
        return SampleType.ACC;
    }

}
