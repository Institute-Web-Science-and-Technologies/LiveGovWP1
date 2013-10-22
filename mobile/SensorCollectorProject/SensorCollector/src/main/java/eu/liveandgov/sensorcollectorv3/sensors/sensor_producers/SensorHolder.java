package eu.liveandgov.sensorcollectorv3.sensors.sensor_producers;

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
