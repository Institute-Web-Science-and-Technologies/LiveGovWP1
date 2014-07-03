package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.Activity;

import java.util.Map;

/**
 * <p>Forwarding of the activity item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class ActivityPackaging extends AbstractPackaging<Activity> {
    /**
     * The one instance of the packaging
     */
    public static final ActivityPackaging ACTIVITY_PACKAGING = new ActivityPackaging();

    /**
     * Hidden constructor
     */
    protected ActivityPackaging() {
    }

    public static final String FIELD_ACTIVITY = "activity";

    @Override
    protected void packRest(Map<String, Object> result, Activity item) {
        result.put(FIELD_ACTIVITY, item.activity);
    }

    @Override
    protected Activity unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final String activity = (String) map.get(FIELD_ACTIVITY);

        return new Activity(timestamp, device, activity);
    }
}
