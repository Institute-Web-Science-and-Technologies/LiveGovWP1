package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class Activity extends Item<String> {
    public static boolean isActivity(String type) {
        return DataCommons.TYPE_ACTIVITY.equals(type);
    }

    public static String assertIsActivity(String type) {
        assert isActivity(type);

        return type;
    }

    public Activity(String type, Header header, String activity) {
        super(assertIsActivity(type), header, activity);
    }
}
