package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

/**
 * <p>Forwarding of the proximity item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class ProximityForwarding extends AbstractForwarding<Proximity> {
    /**
     * The one instance of the forwarding
     */
    public static final ProximityForwarding PROXIMITY_PACKAGING = new ProximityForwarding();

    /**
     * Hidden constructor
     */
    protected ProximityForwarding() {
    }

    public static final String FIELD_KEY = "key";
    public static final String FIELD_IN = "in";
    public static final String FIELD_OF = "of";

    @Override
    protected void forwardRest(Proximity item, Receiver target) {
        target.receive(FIELD_KEY, item.key);
        target.receive(FIELD_IN, item.in);
        target.receive(FIELD_OF, item.of);
    }
}
