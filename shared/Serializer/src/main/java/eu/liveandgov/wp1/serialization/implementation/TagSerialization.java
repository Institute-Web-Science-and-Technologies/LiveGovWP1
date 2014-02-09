package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.implementation.Tag;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class TagSerialization extends Wrapper<Tag, Item<String>> {
    public static final TagSerialization TAG_SERIALIZATION = new TagSerialization();

    private TagSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(Tag tag) {
        return new Item<String>(tag.type, tag.header, escape(tag.data));
    }

    @Override
    protected Tag invertTransform(Item<String> stringItem) {

        return new Tag(stringItem.type, stringItem.header, unescape(stringItem.data));
    }
}
