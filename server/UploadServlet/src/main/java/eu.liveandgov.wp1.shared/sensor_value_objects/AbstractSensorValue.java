package eu.liveandgov.wp1.shared.sensor_value_objects;

public abstract class AbstractSensorValue {
    public final long timestamp;
    public final String id;

    public AbstractSensorValue(long timestamp, String id) {
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getUserId(){
        return id;
    }

    public long getTimestamp(){
        return timestamp;
    }

}
