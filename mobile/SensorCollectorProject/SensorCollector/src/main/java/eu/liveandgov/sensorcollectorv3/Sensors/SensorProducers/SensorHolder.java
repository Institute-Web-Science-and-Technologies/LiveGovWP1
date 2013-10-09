package eu.liveandgov.sensorcollectorv3.Sensors.SensorProducers;

import android.hardware.Sensor;
import android.os.Handler;

/**
 * Created by hartmann on 9/29/13.
 */
public interface SensorHolder {
    /**
     * Starts recording sensor values
     */
    void startRecording();

    /**
     * Stops recording of sensor values
     */
    void stopRecording();

}
