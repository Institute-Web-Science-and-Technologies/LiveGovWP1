package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.GoogleActivitySerialization;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GoogleActivity extends AbstractItem {
    public final String activity;

    @Unit("%")
    public final int confidence;

    public GoogleActivity(long timestamp, String device, String activity, int confidence) {
        super(timestamp, device);
        this.activity = activity;
        this.confidence = confidence;
    }

    public GoogleActivity(Item header, String activity, int confidence) {
        super(header);
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
