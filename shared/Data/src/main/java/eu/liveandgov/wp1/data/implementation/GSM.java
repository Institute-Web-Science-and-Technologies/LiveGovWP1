package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.annotations.Unit;

import java.util.List;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GSM extends Item<Tuple<GSM.GSMStatus, List<GSM.GSMItem>>> {
    public static boolean isGSM(String type) {
        return DataCommons.TYPE_GPS.equals(type);
    }

    public static String assertIsGSM(String type) {
        assert isGSM(type);

        return type;
    }


    public static enum ServiceState {
        EMERGENCY_ONLY, IN_SERVICE, OUT_OF_SERVICE, POWER_OFF, UNKNOWN
    }

    public static enum RoamingState {
        ROAMING, NOT_ROAMING
    }

    public static enum CarrierSelection {
        MANUAL_CARRIER, AUTOMATIC_CARRIER
    }

    public static final class GSMStatus {
        public final ServiceState serviceState;

        public final RoamingState roamingState;

        public final CarrierSelection carrierSelection;

        public final String carrierName;

        public final String signalStrength;

        public GSMStatus(ServiceState serviceState, RoamingState roamingState, CarrierSelection carrierSelection, String carrierName, String signalStrength) {
            this.serviceState = serviceState;
            this.roamingState = roamingState;
            this.carrierSelection = carrierSelection;
            this.carrierName = carrierName;
            this.signalStrength = signalStrength;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GSMStatus gsmStatus = (GSMStatus) o;

            if (carrierName != null ? !carrierName.equals(gsmStatus.carrierName) : gsmStatus.carrierName != null)
                return false;
            if (carrierSelection != gsmStatus.carrierSelection) return false;
            if (roamingState != gsmStatus.roamingState) return false;
            if (serviceState != gsmStatus.serviceState) return false;
            if (signalStrength != null ? !signalStrength.equals(gsmStatus.signalStrength) : gsmStatus.signalStrength != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = serviceState != null ? serviceState.hashCode() : 0;
            result = 31 * result + (roamingState != null ? roamingState.hashCode() : 0);
            result = 31 * result + (carrierSelection != null ? carrierSelection.hashCode() : 0);
            result = 31 * result + (carrierName != null ? carrierName.hashCode() : 0);
            result = 31 * result + (signalStrength != null ? signalStrength.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "GSMStatus{" +
                    "serviceState=" + serviceState +
                    ", roamingState=" + roamingState +
                    ", carrierSelection=" + carrierSelection +
                    ", carrierName='" + carrierName + '\'' +
                    ", signalStrength='" + signalStrength + '\'' +
                    '}';
        }
    }

    public static enum CellType {

        GPRS, EDGE, UMTS, HSDPA, HSUPA, HSPA, UNKNOWN
    }

    public static final class GSMItem {
        public final String cellIdentity;

        public final CellType cellType;

        @Unit("dBm")
        public final int rssi;

        public GSMItem(String cellIdentity, CellType cellType, @Unit("dBm") int rssi) {
            this.cellIdentity = cellIdentity;
            this.cellType = cellType;
            this.rssi = rssi;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GSMItem gsmItem = (GSMItem) o;

            if (rssi != gsmItem.rssi) return false;
            if (cellIdentity != null ? !cellIdentity.equals(gsmItem.cellIdentity) : gsmItem.cellIdentity != null)
                return false;
            if (cellType != gsmItem.cellType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = cellIdentity != null ? cellIdentity.hashCode() : 0;
            result = 31 * result + (cellType != null ? cellType.hashCode() : 0);
            result = 31 * result + rssi;
            return result;
        }

        @Override
        public String toString() {
            return "GSMItem{" +
                    "cellIdentity='" + cellIdentity + '\'' +
                    ", cellType=" + cellType +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    public GSM(String type, Header header, Tuple<GSMStatus, List<GSMItem>> gsmStatusGSMItems) {
        super(assertIsGSM(type), header, gsmStatusGSMItems);
    }
}

