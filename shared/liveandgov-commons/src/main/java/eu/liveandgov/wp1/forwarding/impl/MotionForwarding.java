package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.getFloat;

/**
 * <p>Forwarding of a motion item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class MotionForwarding extends AbstractForwarding<Motion> {
    /**
     * The one instance of the forwarding
     */
    public static final MotionForwarding MOTION_PACKAGING = new MotionForwarding();

    /**
     * Hidden constructor
     */
    protected MotionForwarding() {
    }

    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_Z = "z";

    @Override
    protected void forwardRest(Motion item, Receiver target) {
        target.receive(FIELD_X, item.values[0]);
        target.receive(FIELD_Y, item.values[1]);
        target.receive(FIELD_Z, item.values[2]);
    }
}
