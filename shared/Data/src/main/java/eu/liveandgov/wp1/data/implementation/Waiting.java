package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.annotations.Unit;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public final class Waiting extends Item<Waiting.WaitingStatus> {
    public static boolean isWaiting(String type) {
        return DataCommons.TYPE_WAITING.equals(type);
    }

    public static String assertIsWaiting(String type) {
        assert isWaiting(type);

        return type;
    }

    public static final class WaitingStatus {
        public final String key;

        @Unit("ms")
        public final long waitingTime;

        public final String objectIdentifier;

        public WaitingStatus(String key, long waitingTime, String objectIdentifier) {
            this.key = key;
            this.waitingTime = waitingTime;
            this.objectIdentifier = objectIdentifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WaitingStatus that = (WaitingStatus) o;

            if (waitingTime != that.waitingTime) return false;
            if (key != null ? !key.equals(that.key) : that.key != null) return false;
            if (objectIdentifier != null ? !objectIdentifier.equals(that.objectIdentifier) : that.objectIdentifier != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (int) (waitingTime ^ (waitingTime >>> 32));
            result = 31 * result + (objectIdentifier != null ? objectIdentifier.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "WaitingStatus{" +
                    "key='" + key + '\'' +
                    ", waitingTime=" + waitingTime +
                    ", objectIdentifier='" + objectIdentifier + '\'' +
                    '}';
        }
    }

    public Waiting(String type, Header header, WaitingStatus waitingStatus) {
        super(assertIsWaiting(type), header, waitingStatus);
    }
}
