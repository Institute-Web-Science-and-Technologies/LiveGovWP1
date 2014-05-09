package eu.liveandgov.wp1.forwarding.impl;

import com.google.common.base.Function;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.forwarding.Forwarding;
import eu.liveandgov.wp1.forwarding.ForwardingCommons;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;
import java.util.TreeMap;

import static eu.liveandgov.wp1.packaging.PackagingCommons.*;

/**
 * <p>Abstract serialization formulates the basic forwarding that have to be completed</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public abstract class AbstractForwarding<Data extends Item> implements Forwarding<Data> {


    @Override
    public void forward(Data data, Receiver target) {
        target.receive(ForwardingCommons.FIELD_TYPE, data.getType());
        target.receive(ForwardingCommons.FIELD_TIMESTAMP, data.getTimestamp());
        target.receive(ForwardingCommons.FIELD_DEVICE, data.getDevice());

        forwardRest(data, target);
    }

    /**
     * Forwards all non-basic elements
     * @param data
     * @param target
     */
    protected abstract void forwardRest(Data data, Receiver target);
}
