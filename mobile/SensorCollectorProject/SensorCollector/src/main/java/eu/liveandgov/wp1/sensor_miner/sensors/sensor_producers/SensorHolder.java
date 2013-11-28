package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

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
