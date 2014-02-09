package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.annotations.Optional;

import java.util.List;

/**
 * Created by Lukas Härtel on 09.02.14.
 */
public final class Bluetooth extends Item<List<Bluetooth.BluetoothItem>> {
    public static boolean isBluetooth(String type) {
        return DataCommons.TYPE_BLUETOOTH.equals(type);
    }

    public static String assertIsBluetooth(String type) {
        assert isBluetooth(type);

        return type;
    }

    public static enum BondState {
        NONE, BONDING, BONDED, UNKNOWN
    }

    public static final class BluetoothItem {

        public final String address;

        public final String deviceMajorClass;

        public final String deviceMinorClass;

        public final BondState bondState;

        @Optional
        public final String name;

        @Optional
        public final Short rssi;

        public BluetoothItem(String address, String deviceMajorClass, String deviceMinorClass, BondState bondState, @Optional String name, @Optional Short rssi) {
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

            BluetoothItem that = (BluetoothItem) o;

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
            return "BluetoothItem{" +
                    "address='" + address + '\'' +
                    ", deviceMajorClass='" + deviceMajorClass + '\'' +
                    ", deviceMinorClass='" + deviceMinorClass + '\'' +
                    ", bondState=" + bondState +
                    ", name='" + name + '\'' +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    public Bluetooth(String type, Header header, List<BluetoothItem> bluetoothItems) {
        super(assertIsBluetooth(type), header, bluetoothItems);
    }
}
