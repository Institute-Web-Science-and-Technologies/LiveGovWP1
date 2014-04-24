package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.GSM;

/**
 * <p>Packaging of the GSM item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class GSMPackaging extends UnsupportedPackaging<GSM> {
    /**
     * The one instance of the packaging
     */
    public static final GSMPackaging GSM_PACKAGING = new GSMPackaging();

    /**
     * Hidden constructor
     */
    protected GSMPackaging() {
    }

}
