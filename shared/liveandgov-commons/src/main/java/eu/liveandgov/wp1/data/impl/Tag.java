package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.serialization.impl.TagSerialization;

/**
 * <p>Item representing the manual tag entry</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class Tag extends AbstractItem {
    /**
     * The entered tag
     */
    public final String tag;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param tag       The entered tag
     */
    public Tag(long timestamp, String device, String tag) {
        super(timestamp, device);
        this.tag = tag;
    }


    @Override
    public String getType() {
        return DataCommons.TYPE_TAG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag1 = (Tag) o;

        if (tag != null ? !tag.equals(tag1.tag) : tag1.tag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }

    @Override
    public String createSerializedForm() {
        return TagSerialization.TAG_SERIALIZATION.serialize(this);
    }
}
