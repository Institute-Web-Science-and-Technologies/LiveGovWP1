package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.GoogleActivity;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.getInt;

/**
 * <p>Forwarding of the google play activity item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class GoogleActivityForwarding extends AbstractForwarding<GoogleActivity> {
    /**
     * The one instance of the forwarding
     */
    public static final GoogleActivityForwarding GOOGLE_ACTIVITY_FORWARDING = new GoogleActivityForwarding();

    /**
     * Hidden constructor
     */
    protected GoogleActivityForwarding() {
    }

    public static final String FIELD_ACTIVITY = "activity";

    public static final String FIELD_CONFIDENCE = "confidence";

    @Override
    protected void forwardRest(GoogleActivity item, Receiver target) {
        target.receive(FIELD_ACTIVITY, item.activity);
        target.receive(FIELD_CONFIDENCE, item.confidence);
    }

    @Override
    protected GoogleActivity unForwardRest(String type, long timestamp, String device, Provider source) {
        String activity = (String) source.provide(FIELD_ACTIVITY);
        int confidence = (Integer) source.provide(FIELD_CONFIDENCE);

        return new GoogleActivity(timestamp, device, activity, confidence);
    }
}
