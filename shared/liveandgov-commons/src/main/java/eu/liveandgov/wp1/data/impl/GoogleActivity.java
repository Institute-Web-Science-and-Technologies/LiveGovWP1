package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.GoogleActivitySerialization;

/**
 * <p>Activity recognized by Google Play Services</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class GoogleActivity extends AbstractItem {
    /**
     * The name of the activity recognized by Google Play Services
     */
    public final String activity;

    /**
     * The confidence of the recognized activity in percent
     */
    @Unit("%")
    public final int confidence;

    /**
     * Creates
     *
     * @param timestamp
     * @param device
     * @param activity
     * @param confidence
     */
    public GoogleActivity(long timestamp, String device, String activity, int confidence) {
        super(timestamp, device);
        this.activity = activity;
        this.confidence = confidence;
    }


    @Override
    public String getType() {
        return DataCommons.TYPE_GOOGLE_ACTIVITY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleActivity that = (GoogleActivity) o;

        if (confidence != that.confidence) return false;
        if (activity != null ? !activity.equals(that.activity) : that.activity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = activity != null ? activity.hashCode() : 0;
        result = 31 * result + confidence;
        return result;
    }

    @Override
    public String createSerializedForm() {
        return GoogleActivitySerialization.GOOGLE_ACTIVITY_SERIALIZATION.serialize(this);
    }
}
