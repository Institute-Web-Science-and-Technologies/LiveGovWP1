package eu.liveandgov.wp1.sensor_collector.pps;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityType;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class ProximityEvent {
    public final long time;
    public final String key;
    public final Proximity proximity;

    public ProximityEvent(long time, String key, Proximity proximity) {
        this.time = time;
        this.key = key;
        this.proximity = proximity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProximityEvent that = (ProximityEvent) o;

        if (time != that.time) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (proximity != null ? !proximity.equals(that.proximity) : that.proximity != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (proximity != null ? proximity.hashCode() : 0);
        return result;
    }
}
