package eu.liveandgov.wp1.forwarding.impl;

import com.google.common.base.Function;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.forwarding.Forwarding;
import eu.liveandgov.wp1.forwarding.ForwardingCommons;
import eu.liveandgov.wp1.forwarding.Provider;
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
     *
     * @param data   The source item
     * @param target The receiver of the forwarding
     */
    protected abstract void forwardRest(Data data, Receiver target);

    @Override
    public Data unForward(Provider source) {
        String type = (String) source.provide(ForwardingCommons.FIELD_TYPE);
        long timestamp = (Long) source.provide(ForwardingCommons.FIELD_TIMESTAMP);
        String device = (String) source.provide(ForwardingCommons.FIELD_DEVICE);

        return unForwardRest(type, timestamp, device, source);
    }

    /**
     * Unforwards all non-basic elements
     *
     * @param type      The type
     * @param timestamp The timestamp
     * @param device    The device
     * @param source    The source provider
     * @return Returns the un-forwarded item
     */
    protected abstract Data unForwardRest(String type, long timestamp, String device, Provider source);
}
