package eu.liveandgov.wp1.shared.sensors.sensor_value_objects;

import eu.liveandgov.wp1.shared.sensors.SampleType;

/**
 * User: hartmann
 * Date: 10/22/13
 */
public interface SensorValueInterface {

    /**
     * Return sample in SSF Format
     * @return ssfRow
     */
    public String toSSF();

    /**
     * Return type of sensor
     * @return sensorType
     */
    public SampleType getType();

    /**
     * Return user id of samples
     * @return userId
     */
    public String getUserId();

    /**
     * Return time stamp of
     * @return timestamp
     */
    long getTimestamp();
}
