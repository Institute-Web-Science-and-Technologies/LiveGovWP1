package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.escape;
import static eu.liveandgov.wp1.serialization.SerializationCommons.nextString;
import static eu.liveandgov.wp1.serialization.SerializationCommons.unescape;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class TagSerialization extends AbstractSerialization<Tag> {
    public static final TagSerialization TAG_SERIALIZATION = new TagSerialization();

    private TagSerialization() {
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
