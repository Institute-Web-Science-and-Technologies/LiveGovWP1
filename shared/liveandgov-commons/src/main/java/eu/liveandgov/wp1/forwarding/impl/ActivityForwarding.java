package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

/**
 * <p>Forwarding of the activity item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class ActivityForwarding extends AbstractForwarding<Activity> {
    /**
     * The one instance of the forwarding
     */
    public static final ActivityForwarding ACTIVITY_FORWARDING = new ActivityForwarding();

    /**
     * Hidden constructor
     */
    protected ActivityForwarding() {
    }

    public static final String FIELD_ACTIVITY = "activity";

    @Override
    protected void forwardRest(Activity activity, Receiver target) {
        target.receive(FIELD_ACTIVITY, activity.activity);
    }

    @Override
    protected Activity unForwardRest(String type, long timestamp, String device, Provider source) {
        String activity = (String) source.provide(FIELD_ACTIVITY);

        return new Activity(timestamp, device, activity);
    }
}
