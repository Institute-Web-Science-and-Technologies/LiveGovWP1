package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

/**
 * <p>Forwarding of the waiting item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class WaitingForwarding extends AbstractForwarding<Waiting> {
    /**
     * The one instance of the forwarding
     */
    public static final WaitingForwarding WAITING_FORWARDING = new WaitingForwarding();

    /**
     * Hidden constructor
     */
    protected WaitingForwarding() {
    }

    public static final String FIELD_KEY = "key";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_AT = "at";

    @Override
    protected void forwardRest(Waiting item, Receiver target) {
        target.receive(FIELD_KEY, item.key);
        target.receive(FIELD_DURATION, item.duration);
        target.receive(FIELD_AT, item.at);
    }

    @Override
    protected Waiting unForwardRest(String type, long timestamp, String device, Provider source) {
        String key = (String) source.provide(FIELD_KEY);
        long duration = (Long) source.provide(FIELD_DURATION);
        String at = (String) source.provide(FIELD_AT);

        return new Waiting(timestamp, device, key, duration, at);
    }
}
