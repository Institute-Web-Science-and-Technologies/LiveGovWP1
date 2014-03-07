package eu.liveandgov.wp1.data;

/**
 * Created by Lukas Härtel on 11.02.14.
 */
public abstract class AbstractItem implements Item {
    private final long timestamp;

    private final String device;

    private String cacheSerializedForm;

    protected AbstractItem(long timestamp, String device) {
        this.timestamp = timestamp;
        this.device = device;

        cacheSerializedForm = null;
    }

    protected AbstractItem(Item header) {
        this.timestamp = header.getTimestamp();
        this.device = header.getDevice();

        cacheSerializedForm = null;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDevice() {
        return device;
    }

    /**
     * {@inheritDoc} <br/>
     * Calculated only once by AbstractItem and reused due to final assignment of fields
     */
    @Override
    public String toSerializedForm() {
        if (cacheSerializedForm == null) {
            cacheSerializedForm = createSerializedForm();
        }
        return cacheSerializedForm;
    }

    protected abstract String createSerializedForm();
}
