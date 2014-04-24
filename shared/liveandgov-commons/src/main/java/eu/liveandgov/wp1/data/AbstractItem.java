package eu.liveandgov.wp1.data;

/**
 * <p>Abstract item implementing the base features and providing a deferred lazy serialization</p>
 * Created by Lukas Härtel on 11.02.14.
 */
public abstract class AbstractItem implements Item {
    /**
     * Backing for the item time
     */
    private final long timestamp;

    /**
     * Backing for the item device
     */
    private final String device;

    /**
     * Store for the serialized form
     */
    private String cacheSerializedForm;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     */
    protected AbstractItem(long timestamp, String device) {
        this.timestamp = timestamp;
        this.device = device;

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

    /**
     * Calculates the serialized form of the item
     *
     * @return Returns a string representing the serialized form
     */
    protected abstract String createSerializedForm();

    @Override
    public String toString() {
        return createSerializedForm();
    }
}
