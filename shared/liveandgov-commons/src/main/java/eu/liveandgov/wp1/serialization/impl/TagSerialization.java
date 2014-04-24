package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Tag;

import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.escape;
import static eu.liveandgov.wp1.serialization.SerializationCommons.nextString;

/**
 * <p>Serialization of the tag item</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class TagSerialization extends AbstractSerialization<Tag> {
    /**
     * The one instance of the serialization
     */
    public static final TagSerialization TAG_SERIALIZATION = new TagSerialization();

    /**
     * Hidden constructor
     */
    protected TagSerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, Tag tag) {
        stringBuilder.append(escape(tag.tag));
    }

    @Override
    protected Tag deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        final String tag = nextString(scanner);

        return new Tag(timestamp, device, tag);
    }
}
