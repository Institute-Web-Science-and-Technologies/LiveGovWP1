package eu.liveandgov.wp1.data;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public abstract class AbstractItem implements Item {
    private final long timestamp;

    private final String device;

    protected AbstractItem(long timestamp, String device) {
        this.timestamp = timestamp;
        this.device = device;
    }

    protected AbstractItem(Item header) {
        this.timestamp = header.getTimestamp();
        this.device = header.getDevice();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDevice() {
        return device;
    }
}
