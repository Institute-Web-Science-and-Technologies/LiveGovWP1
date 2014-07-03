package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.Proximity;

import java.util.Map;

/**
 * <p>Forwarding of the proximity item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class ProximityPackaging extends AbstractPackaging<Proximity> {
    /**
     * The one instance of the packaging
     */
    public static final ProximityPackaging PROXIMITY_PACKAGING = new ProximityPackaging();

    /**
     * Hidden constructor
     */
    protected ProximityPackaging() {
    }

    public static final String FIELD_KEY = "key";
    public static final String FIELD_IN = "in";
    public static final String FIELD_OF = "of";

    @Override
    protected void packRest(Map<String, Object> result, Proximity item) {
        result.put(FIELD_KEY, item.key);
        result.put(FIELD_IN, item.in);
        result.put(FIELD_OF, item.of);
    }

    @Override
    protected Proximity unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final String key = (String) map.get(FIELD_KEY);
        final boolean in = (Boolean) map.get(FIELD_IN);
        final String of = (String) map.get(FIELD_OF);

        return new Proximity(timestamp, device, key, in, of);
    }
}
