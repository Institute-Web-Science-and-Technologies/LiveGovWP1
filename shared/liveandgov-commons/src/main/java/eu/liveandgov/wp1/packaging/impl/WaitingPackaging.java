package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.Waiting;

import java.util.Map;

/**
 * <p>Forwarding of the waiting item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class WaitingPackaging extends AbstractPackaging<Waiting> {
    /**
     * The one instance of the packaging
     */
    public static final WaitingPackaging WAITING_PACKAGING = new WaitingPackaging();

    /**
     * Hidden constructor
     */
    protected WaitingPackaging() {
    }

    public static final String FIELD_KEY = "key";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_AT = "at";

    @Override
    protected void packRest(Map<String, Object> result, Waiting item) {
        result.put(FIELD_KEY, item.key);
        result.put(FIELD_DURATION, item.duration);
        result.put(FIELD_AT, item.at);
    }

    @Override
    protected Waiting unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final String key = (String) map.get(FIELD_KEY);
        final long duration = (Long) map.get(FIELD_DURATION);
        final String at = (String) map.get(FIELD_AT);

        return new Waiting(timestamp, device, key, duration, at);
    }
}
