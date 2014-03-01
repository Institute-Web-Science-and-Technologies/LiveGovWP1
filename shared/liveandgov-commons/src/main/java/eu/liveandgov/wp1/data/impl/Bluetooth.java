package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Optional;
import eu.liveandgov.wp1.serialization.impl.BluetoothSerialization;

import java.util.Arrays;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public class Bluetooth extends AbstractItem {
    public static enum BondState {
        NONE, BONDING, BONDED, UNKNOWN
    }

    public static final class Item {
        public static final Item[] EMPTY_ARRAY = new Item[0];

        public final String address;

        public final String deviceMajorClass;

        public final String deviceMinorClass;

        public final BondState bondState;

        @Optional
        public final String name;

        @Optional
        public final Short rssi;

        public Item(String address, String deviceMajorClass, String deviceMinorClass, BondState bondState, @Optional String name, @Optional Short rssi) {
            this.address = address;
            this.deviceMajorClass = deviceMajorClass;
            this.deviceMinorClass = deviceMinorClass;
            this.bondState = bondState;
            this.name = name;
            this.rssi = rssi;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item that = (Item) o;

            if (address != null ? !address.equals(that.address) : that.address != null) return false;
            if (bondState != that.bondState) return false;
            if (deviceMajorClass != null ? !deviceMajorClass.equals(that.deviceMajorClass) : that.deviceMajorClass != null)
                return false;
            if (deviceMinorClass != null ? !deviceMinorClass.equals(that.deviceMinorClass) : that.deviceMinorClass != null)
                return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            if (rssi != null ? !rssi.equals(that.rssi) : that.rssi != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = address != null ? address.hashCode() : 0;
            result = 31 * result + (deviceMajorClass != null ? deviceMajorClass.hashCode() : 0);
            result = 31 * result + (deviceMinorClass != null ? deviceMinorClass.hashCode() : 0);
            result = 31 * result + (bondState != null ? bondState.hashCode() : 0);
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (rssi != null ? rssi.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "address='" + address + '\'' +
                    ", deviceMajorClass='" + deviceMajorClass + '\'' +
                    ", deviceMinorClass='" + deviceMinorClass + '\'' +
                    ", bondState=" + bondState +
                    ", name='" + name + '\'' +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    public final Item[] items;

    public Bluetooth(long timestamp, String device, Item[] items) {
        super(timestamp, device);

        this.items = items;
    }

    public Bluetooth(eu.liveandgov.wp1.data.Item header, Item[] items) {
        super(header);

        this.items = items;
    }

    @Override
    public String getType() {
        return DataCommons.TYPE_BLUETOOTH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bluetooth bluetooth = (Bluetooth) o;

        if (!Arrays.equals(items, bluetooth.items)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return items != null ? Arrays.hashCode(items) : 0;
    }

    @Override
    public String toString() {
        return "Bluetooth{" +
                "items=" + Arrays.toString(items) +
                '}';
    }

    @Override
    public String createSerializedForm() {
        return BluetoothSerialization.BLUETOOTH_SERIALIZATION.serialize(this);
    }
}
