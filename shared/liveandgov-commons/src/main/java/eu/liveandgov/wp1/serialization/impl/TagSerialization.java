package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.serialization.Wrapper;

import static eu.liveandgov.wp1.serialization.SerializationCommons.escape;
import static eu.liveandgov.wp1.serialization.SerializationCommons.unescape;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class TagSerialization extends Wrapper<Tag, Arbitrary> {
    public static final TagSerialization TAG_SERIALIZATION = new TagSerialization();

    private TagSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(Tag tag) {
        return new Arbitrary(tag, tag.getType(), escape(tag.tag));
    }

    @Override
    protected Tag invertTransform(Arbitrary item) {

        return new Tag(item, unescape(item.value));
    }
}
