package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.GSMSerialization;

import java.util.Arrays;

/**
 * <p>State of the GSM environment</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public class GSM extends AbstractItem {
    /**
     * Service state of the GSM unit
     */
    public static enum ServiceState {
        /**
         * Only emergency calls
         */
        EMERGENCY_ONLY,
        /**
         * In service
         */
        IN_SERVICE,
        /**
         * Out of service
         */
        OUT_OF_SERVICE,
        /**
         * Power off
         */
        POWER_OFF,
        /**
         * Unknown state
         */
        UNKNOWN
    }

    /**
     * Roaming state
     */
    public static enum RoamingState {
        /**
         * Roaming in other network
         */
        ROAMING,
        /**
         * In home network
         */
        NOT_ROAMING
    }

    /**
     * Carrier selection state
     */
    public static enum CarrierSelection {
        /**
         * Data manually entered
         */
        MANUAL_CARRIER,
        /**
         * Automatic selection enabled
         */
        AUTOMATIC_CARRIER
    }

    /**
     * Type of a mobile cell
     */
    public static enum CellType {
        /**
         * GPRS cell
         */
        GPRS,
        /**
         * EDGE cell
         */
        EDGE,
        /**
         * UMTS cell
         */
        UMTS,
        /**
         * HSDPA cell
         */
        HSDPA,
        /**
         * HSUPA cell
         */
        HSUPA,
        /**
         * HSPA cell
         */
        HSPA,
        /**
         * Unknown cell type
         */
        UNKNOWN
    }

    /**
     * Item of the GSM environment
     */
    public static final class Item {
        /**
         * Empty array of items
         */
        public static final Item[] EMPTY_ARRAY = new Item[0];

        /**
         * Identity string of the cell
         */
        public final String cellIdentity;

        /**
         * Type of the cell
         */
        public final CellType cellType;

        /**
         * Receive signal strength indicator
         */
        @Unit("dBm")
        public final int rssi;

        /**
         * Creates a new instance with the given values
         *
         * @param cellIdentity Identity string of the cell
         * @param cellType     Type of the cell
         * @param rssi         Receive signal strength indicator
         */
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

    /**
     * Current service state
     */
    public final ServiceState serviceState;

    /**
     * Roaming state
     */
    public final RoamingState roamingState;

    /**
     * Carrier selection mode
     */
    public final CarrierSelection carrierSelection;

    /**
     * Carrier name
     */
    public final String carrierName;

    /**
     * Signal strength text
     */
    public final String signalStrength;

    /**
     * Items forming the environment
     */
    public final Item[] items;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp        Time of the item
     * @param device           Device of the item
     * @param serviceState     Current service state
     * @param roamingState     Roaming state
     * @param carrierSelection Carrier selection mode
     * @param carrierName      Carrier name
     * @param signalStrength   Signal strength text
     * @param items            Items forming the environment
     */
    public GSM(long timestamp, String device, ServiceState serviceState, RoamingState roamingState, CarrierSelection carrierSelection, String carrierName, String signalStrength, Item[] items) {
        super(timestamp, device);
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
    public String createSerializedForm() {
        return GSMSerialization.GSM_SERIALIZATION.serialize(this);
    }
}

