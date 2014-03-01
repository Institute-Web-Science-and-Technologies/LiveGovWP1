package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.GSMSerialization;

import java.util.Arrays;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class GSM extends AbstractItem {
    public static enum ServiceState {
        EMERGENCY_ONLY, IN_SERVICE, OUT_OF_SERVICE, POWER_OFF, UNKNOWN
    }

    public static enum RoamingState {
        ROAMING, NOT_ROAMING
    }

    public static enum CarrierSelection {
        MANUAL_CARRIER, AUTOMATIC_CARRIER
    }

    public static enum CellType {

        GPRS, EDGE, UMTS, HSDPA, HSUPA, HSPA, UNKNOWN
    }

    public static final class Item {
        public static final Item[] EMPTY_ARRAY = new Item[0];

        public final String cellIdentity;

        public final CellType cellType;

        @Unit("dBm")
        public final int rssi;

        public Item(String cellIdentity, CellType cellType, @Unit("dBm") int rssi) {
            this.cellIdentity = cellIdentity;
            this.cellType = cellType;
            this.rssi = rssi;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (rssi != item.rssi) return false;
            if (cellIdentity != null ? !cellIdentity.equals(item.cellIdentity) : item.cellIdentity != null)
                return false;
            if (cellType != item.cellType) return false;

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
            return "Item{" +
                    "cellIdentity='" + cellIdentity + '\'' +
                    ", cellType=" + cellType +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    public final ServiceState serviceState;

    public final RoamingState roamingState;

    public final CarrierSelection carrierSelection;

    public final String carrierName;

    public final String signalStrength;

    public final Item[] items;

    public GSM(long timestamp, String device, ServiceState serviceState, RoamingState roamingState, CarrierSelection carrierSelection, String carrierName, String signalStrength, Item[] items) {
        super(timestamp, device);
        this.serviceState = serviceState;
        this.roamingState = roamingState;
        this.carrierSelection = carrierSelection;
        this.carrierName = carrierName;
        this.signalStrength = signalStrength;
        this.items = items;
    }

    public GSM(eu.liveandgov.wp1.data.Item header, ServiceState serviceState, RoamingState roamingState, CarrierSelection carrierSelection, String carrierName, String signalStrength, Item[] items) {
        super(header);
        this.serviceState = serviceState;
        this.roamingState = roamingState;
        this.carrierSelection = carrierSelection;
        this.carrierName = carrierName;
        this.signalStrength = signalStrength;
        this.items = items;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_GSM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GSM gsm = (GSM) o;

        if (carrierName != null ? !carrierName.equals(gsm.carrierName) : gsm.carrierName != null) return false;
        if (carrierSelection != gsm.carrierSelection) return false;
        if (!Arrays.equals(items, gsm.items)) return false;
        if (roamingState != gsm.roamingState) return false;
        if (serviceState != gsm.serviceState) return false;
        if (signalStrength != null ? !signalStrength.equals(gsm.signalStrength) : gsm.signalStrength != null)
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
        result = 31 * result + (items != null ? Arrays.hashCode(items) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GSM{" +
                "serviceState=" + serviceState +
                ", roamingState=" + roamingState +
                ", carrierSelection=" + carrierSelection +
                ", carrierName='" + carrierName + '\'' +
                ", signalStrength='" + signalStrength + '\'' +
                ", items=" + Arrays.toString(items) +
                '}';
    }

    @Override
    public String createSerializedForm() {
        return GSMSerialization.GSM_SERIALIZATION.serialize(this);
    }
}

