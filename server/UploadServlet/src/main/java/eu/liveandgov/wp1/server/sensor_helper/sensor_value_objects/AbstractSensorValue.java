package eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects;

import eu.liveandgov.wp1.server.sensor_helper.SampleType;

public abstract class AbstractSensorValue {
    public final long timestamp;
    public final String id;

    public AbstractSensorValue(long timestamp, String id) {
        this.timestamp = timestamp;
        this.id = id;
    }

}
