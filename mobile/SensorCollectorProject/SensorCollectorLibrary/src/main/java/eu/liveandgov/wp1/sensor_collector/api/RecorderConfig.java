package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;

/**
 * <p>Configuration corresponding to a registered recorder</p>
 * Created by lukashaertel on 30.11.2014.
 */
public class RecorderConfig implements Parcelable, Serializable {
    public static final Creator<RecorderConfig> CREATOR = new Creator<RecorderConfig>() {
        @Override
        public RecorderConfig createFromParcel(Parcel source) {
            return new RecorderConfig(source);
        }

        @Override
        public RecorderConfig[] newArray(int size) {
            return new RecorderConfig[size];
        }
    };

    /**
     * The item types this recorder is recording
     */
    public final ImmutableSet<String> itemTypes;

    /**
     * The time span this recorder is recording over, in milliseconds
     */
    public final long timeSpanMs;

    /**
     * The maximum items to record
     */
    public final long maximum;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(itemTypes);
        dest.writeLong(timeSpanMs);
        dest.writeLong(maximum);
    }

    private RecorderConfig(Parcel source) {
        @SuppressWarnings("unchecked")
        ImmutableSet<String> castItemTypes = (ImmutableSet<String>) source.readSerializable();

        itemTypes = castItemTypes;
        timeSpanMs = source.readLong();
        maximum = source.readLong();
    }

    public RecorderConfig(ImmutableSet<String> itemTypes, long timeSpanMs, long maximum) {
        this.itemTypes = itemTypes;
        this.timeSpanMs = timeSpanMs;
        this.maximum = maximum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecorderConfig that = (RecorderConfig) o;

        if (maximum != that.maximum) return false;
        if (timeSpanMs != that.timeSpanMs) return false;
        if (itemTypes != null ? !itemTypes.equals(that.itemTypes) : that.itemTypes != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemTypes != null ? itemTypes.hashCode() : 0;
        result = 31 * result + (int) (timeSpanMs ^ (timeSpanMs >>> 32));
        result = 31 * result + (int) (maximum ^ (maximum >>> 32));
        return result;
    }
}
