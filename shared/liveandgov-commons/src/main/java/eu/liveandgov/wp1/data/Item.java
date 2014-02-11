package eu.liveandgov.wp1.data;

/**
 * <p>Item in the ssf stream</p>
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
}
