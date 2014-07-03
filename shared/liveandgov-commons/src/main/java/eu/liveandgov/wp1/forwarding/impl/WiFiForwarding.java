package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.WiFi;

/**
 * <p>Forwarding of the WiFi item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class WiFiForwarding extends UnsupportedForwarding<WiFi> {
    /**
     * The one instance of the forwarding
     */
    public static final WiFiForwarding WI_FI_FORWARDING = new WiFiForwarding();

    /**
     * Hidden constructor
     */
    protected WiFiForwarding() {
    }
}
