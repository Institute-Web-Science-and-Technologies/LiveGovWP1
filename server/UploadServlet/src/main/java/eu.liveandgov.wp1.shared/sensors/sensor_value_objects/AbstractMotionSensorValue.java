package eu.liveandgov.wp1.shared.sensors.sensor_value_objects;

public abstract class AbstractMotionSensorValue extends AbstractSensorValue {
    public final float x;
	public final float y;
	public final float z;

    public AbstractMotionSensorValue(long timestamp, String id, float x, float y, float z) {
        super(timestamp, id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns last part of ssf row. Sample type is missing!
     * @return ssfRowPart
     */
    protected String baseSSF() {
        return String.format("%d,%s,%f %f %f", timestamp, id, x,y,z);
    }
}
