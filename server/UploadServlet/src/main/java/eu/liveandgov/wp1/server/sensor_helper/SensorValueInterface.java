package eu.liveandgov.wp1.server.sensor_helper;

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

}
