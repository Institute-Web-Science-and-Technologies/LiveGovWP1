package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.GoogleActivity;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.*;

/**
 * <p>Packaging of the google play activity item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class GoogleActivityPackaging extends AbstractPackaging<GoogleActivity> {
    /**
     * The one instance of the packaging
     */
    public static final GoogleActivityPackaging GOOGLE_ACTIVITY_PACKAGING = new GoogleActivityPackaging();

    /**
     * Hidden constructor
     */
    protected GoogleActivityPackaging() {
    }

    public static final String FIELD_ACTIVITY = "activity";

    public static final String FIELD_CONFIDENCE = "confidence";

    @Override
    protected void pack(Map<String, Object> result, GoogleActivity item) {
        result.put(FIELD_ACTIVITY, item.activity);
        result.put(FIELD_CONFIDENCE, item.confidence);
    }

    @Override
    protected GoogleActivity unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final String activity = (String) map.get(FIELD_ACTIVITY);
        final int confidence = getInt(map.get(FIELD_CONFIDENCE));

        return new GoogleActivity(timestamp, device, activity, confidence);
    }
}
