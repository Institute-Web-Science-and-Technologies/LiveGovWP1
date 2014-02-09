package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public final class Proximity extends Item<Proximity.ProximityStatus> {
    public static boolean isProximity(String type) {
        return DataCommons.TYPE_PROXIMITY.equals(type);
    }

    public static String assertIsProximity(String type) {
        assert isProximity(type);

        return type;
    }

    public static enum ProximityType {
        IN_PROXIMITY, NOT_IN_PROXIMITY, NO_DECISION
    }

    public static final class ProximityStatus {
        public final String key;

        public final ProximityType proximityType;

        public final String objectIdentifier;

        public ProximityStatus(String key, ProximityType proximityType, String objectIdentifier) {
            this.key = key;
            this.proximityType = proximityType;
            this.objectIdentifier = objectIdentifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProximityStatus that = (ProximityStatus) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;
            if (objectIdentifier != null ? !objectIdentifier.equals(that.objectIdentifier) : that.objectIdentifier != null)
                return false;
            if (proximityType != that.proximityType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (proximityType != null ? proximityType.hashCode() : 0);
            result = 31 * result + (objectIdentifier != null ? objectIdentifier.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "ProximityStatus{" +
                    "key='" + key + '\'' +
                    ", proximityType=" + proximityType +
                    ", objectIdentifier='" + objectIdentifier + '\'' +
                    '}';
        }
    }

    public Proximity(String type, Header header, ProximityStatus proximityStatus) {
        super(assertIsProximity(type), header, proximityStatus);
    }
}
