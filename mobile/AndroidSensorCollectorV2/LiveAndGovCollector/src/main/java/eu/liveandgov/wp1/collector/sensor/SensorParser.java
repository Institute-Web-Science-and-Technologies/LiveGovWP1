package eu.liveandgov.wp1.collector.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Created by hartmann on 9/15/13.
 */
public class SensorParser {

    public static String parse(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            return String.format("ACC@%d : %f %f %f", event.timestamp / 1000 , event.values[0], event.values[1], event.values[2]);
        } if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            return String.format("LACC@%d : %f %f %f", event.timestamp / 1000 , event.values[0], event.values[1], event.values[2]);
        }
        return null;
    }
}
