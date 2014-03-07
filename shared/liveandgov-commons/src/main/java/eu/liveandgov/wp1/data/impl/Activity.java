package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.serialization.impl.ActivitySerialization;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class Activity extends AbstractItem {
    public final String activity;

    public Activity(long timestamp, String device, String activity) {
        super(timestamp, device);
        this.activity = activity;
    }

    public Activity(Item header, String activity) {
        super(header);
        this.activity = activity;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_ACTIVITY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity1 = (Activity) o;

        if (activity != null ? !activity.equals(activity1.activity) : activity1.activity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return activity != null ? activity.hashCode() : 0;
    }

    @Override
    public String createSerializedForm() {
        return ActivitySerialization.ACTIVITY_SERIALIZATION.serialize(this);
    }
}
