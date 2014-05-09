package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.forwarding.Forwarding;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

/**
 * <p>Abstract base class representing an unsupported forwarding</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class UnsupportedForwarding<Data> implements Forwarding<Data> {

    @Override
    public void forward(Data data, Receiver target) {
        throw new UnsupportedOperationException("Forwarding not supported");
    }
}
