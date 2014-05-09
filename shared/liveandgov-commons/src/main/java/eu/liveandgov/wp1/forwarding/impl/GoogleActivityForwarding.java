package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.GoogleActivity;
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
    public static final GoogleActivityForwarding GOOGLE_ACTIVITY_PACKAGING = new GoogleActivityForwarding();

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
}
