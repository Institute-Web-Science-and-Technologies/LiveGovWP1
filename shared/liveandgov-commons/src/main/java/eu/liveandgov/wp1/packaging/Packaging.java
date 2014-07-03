package eu.liveandgov.wp1.packaging;

import java.util.Map;

@Deprecated
/**
 * <p>Base  class for packaging and un-packaging to and from maps</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public interface Packaging<Data> {
    /**
     * Packs the data into a map
     *
     * @param data The data to packRest
     * @return Returns the packaged data
     */
    public Map<String, ?> pack(Data data);

    /**
     * Un-packs the data from a map
     *
     * @param map The map to un-packRest from
     * @return Returns the data object
     */
    public Data unPack(Map<String, ?> map);
}
