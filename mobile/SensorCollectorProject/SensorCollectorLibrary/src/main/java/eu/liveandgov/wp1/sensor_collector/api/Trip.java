package eu.liveandgov.wp1.sensor_collector.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public class Trip implements Parcelable, Comparable<Trip> {
    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public static final long SPECIAL_TIME_UNSET = Long.MIN_VALUE;

    public final
    @NonNull
    String userId;

    public final
    @NonNull
    String userSecret;

    public final long startTime;

    public final long endTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userSecret);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
    }

    private Trip(Parcel source) {
        userId = source.readString();
        userSecret = source.readString();
        startTime = source.readLong();
        endTime = source.readLong();
    }

    public Trip(@NonNull String userId, @NonNull String userSecret, long startTime, long endTime) {
        this.userId = userId;
        this.userSecret = userSecret;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;

        Trip trip = (Trip) o;

        if (endTime != trip.endTime) return false;
        if (startTime != trip.startTime) return false;
        if (!userId.equals(trip.userId)) return false;
        if (!userSecret.equals(trip.userSecret)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + userSecret.hashCode();
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }

    @Override
    public int compareTo(@NonNull Trip another) {
        return ComparisonChain.start()
                .compare(userId, another.userId)
                .compare(userSecret, another.userSecret)
                .compare(startTime, another.startTime)
                .compare(endTime, another.endTime)
                .result();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("userSecret", userSecret)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .toString();
    }
}
