package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class Tag extends Item<String> {
    public static boolean isTag(String type) {
        return DataCommons.TYPE_TAG.equals(type);
    }

    public static String assertIsTag(String type) {
        assert isTag(type);

        return type;
    }

    public Tag(String type, Header header, String s) {
        super(assertIsTag(type), header, s);
    }
}
