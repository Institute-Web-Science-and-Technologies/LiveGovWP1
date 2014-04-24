package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.Tag;

import java.util.Map;

/**
 * <p>Packaging of the tag item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class TagPackaging extends AbstractPackaging<Tag> {
    /**
     * The one instance of the packaging
     */
    public static final TagPackaging TAG_PACKAGING = new TagPackaging();

    /**
     * Hidden constructor
     */
    protected TagPackaging() {
    }

    public static final String FIELD_TAG = "tag";

    @Override
    protected void pack(Map<String, Object> result, Tag item) {
        result.put(FIELD_TAG, item.tag);
    }

    @Override
    protected Tag unPackRest(String type, long timestamp, String device, Map<String, ?> map) {
        final String tag = (String) map.get(FIELD_TAG);

        return new Tag(timestamp, device, tag);
    }
}
