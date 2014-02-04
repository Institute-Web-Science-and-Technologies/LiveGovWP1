package eu.liveandgov.wp1.sensor_collector.waiting;

/**
 * Created by lukashaertel on 04.02.14.
 */
public class WaitingEvent {
    public final long time;
    public final long duration;
    public final String key;
    public final String objectIdentity;

    public WaitingEvent(long time, String key, long duration, String objectIdentity) {
        this.time = time;
        this.key = key;
        this.objectIdentity = objectIdentity;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WaitingEvent that = (WaitingEvent) o;

        if (duration != that.duration) return false;
        if (time != that.time) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (objectIdentity != null ? !objectIdentity.equals(that.objectIdentity) : that.objectIdentity != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (objectIdentity != null ? objectIdentity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WaitingEvent{" +
                "time=" + time +
                ", duration=" + duration +
                ", key='" + key + '\'' +
                ", objectIdentity='" + objectIdentity + '\'' +
                '}';
    }
}
