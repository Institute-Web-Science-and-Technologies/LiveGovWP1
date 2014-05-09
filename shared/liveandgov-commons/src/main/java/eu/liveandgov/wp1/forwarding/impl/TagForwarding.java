package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

/**
 * <p>Forwarding of the tag item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class TagForwarding extends AbstractForwarding<Tag> {
    /**
     * The one instance of the forwarding
     */
    public static final TagForwarding TAG_PACKAGING = new TagForwarding();

    /**
     * Hidden constructor
     */
    protected TagForwarding() {
    }

    public static final String FIELD_TAG = "tag";

    @Override
    protected void forwardRest(Tag item, Receiver target) {
        target.receive(FIELD_TAG, item.tag);
    }
}
