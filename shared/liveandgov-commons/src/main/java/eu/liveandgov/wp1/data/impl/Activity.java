package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.serialization.impl.ActivitySerialization;

/**
 * <p>Represents an activity recognized by the HAR pipeline</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class Activity extends AbstractItem {
    /**
     * The name of the activity recognized by the HAR pipeline
     */
    public final String activity;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param activity  Recognized activity
     */
    public Activity(long timestamp, String device, String activity) {
        super(timestamp, device);
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
