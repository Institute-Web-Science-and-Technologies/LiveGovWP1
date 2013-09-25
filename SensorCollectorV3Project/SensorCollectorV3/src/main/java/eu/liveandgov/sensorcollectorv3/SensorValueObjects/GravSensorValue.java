package eu.liveandgov.sensorcollectorv3.SensorValueObjects;

/**
 * Created by hartmann on 9/23/13.
 */
public class GravSensorValue extends AccSensorValue {
    @Override
    public String toString(){
        return String.format("GRAV - type:%s ts:%d id:%s x:%f y:%f z:%f", type, timestamp, id, x,y,z);
    }
}
