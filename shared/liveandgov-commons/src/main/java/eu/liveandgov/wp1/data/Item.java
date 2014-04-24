package eu.liveandgov.wp1.data;

/**
 * <p>Item in the SSF stream</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public interface Item {
    /**
     * Type of the item
     */
    public String getType();

    /**
     * Time of the item
     */
    public long getTimestamp();

    /**
     * Device of the item
     */
    public String getDevice();

    /**
     * Returns the serialized form of this item, has to have a corresponding reconstruction in the serializer package
     */
    public String toSerializedForm();
}
