package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.GSM;

/**
 * <p>Forwarding of the GSM item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class GSMForwarding extends UnsupportedForwarding<GSM> {
    /**
     * The one instance of the forwarding
     */
    public static final GSMForwarding GSM_FORWARDING = new GSMForwarding();

    /**
     * Hidden constructor
     */
    protected GSMForwarding() {
    }

}
