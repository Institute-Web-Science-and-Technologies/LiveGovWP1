package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.WiFi;

/**
 * <p>Packaging of the WiFi item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class WiFiPackaging extends UnsupportedPackaging<WiFi> {
    /**
     * The one instance of the packaging
     */
    public static final WiFiPackaging WI_FI_PACKAGING = new WiFiPackaging();

    /**
     * Hidden constructor
     */
    protected WiFiPackaging() {
    }
}
