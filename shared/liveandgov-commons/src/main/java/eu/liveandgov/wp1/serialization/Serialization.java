package eu.liveandgov.wp1.serialization;

/**
 * <p>Base  class for serializing and de-serializing to and from strings</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public interface Serialization<Data> {
    /**
     * Serializes the data into a string
     *
     * @param data The data to serialize
     * @return Returns the serialized data
     */
    public String serialize(Data data);

    /**
     * De-serializes the data from a string
     *
     * @param string The string to de-serialize from
     * @return Returns the data object
     */
    public Data deSerialize(String string);
}
