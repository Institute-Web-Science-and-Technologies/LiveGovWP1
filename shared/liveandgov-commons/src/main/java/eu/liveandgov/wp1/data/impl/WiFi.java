package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.serialization.impl.WiFiSerialization;

import java.util.Arrays;

/**
 * <p>State of the WiFi environment</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class WiFi extends AbstractItem {
    /**
     * Item forming the WiFi environment
     */
    public static final class Item {

        /**
         * SSID of the access point
         */
        public final String ssid;

        /**
         * BSSID of the access point
         */
        public final String bssid;

        /**
         * Frequency used
         */
        @Unit("MHz")
        public final int frequency;

        /**
         * Signal strength
         */
        @Unit("dBm")
        public final int level;

        /**
         * Creates a new instance with the given values
         *
         * @param ssid      SSID of the access point
         * @param bssid     BSSID of the access point
         * @param frequency Frequency used
         * @param level     Signal strength
         */
        public Item(String ssid, String bssid, @Unit("MHz") int frequency, @Unit("dBm") int level) {
            this.ssid = ssid;
            this.bssid = bssid;
            this.frequency = frequency;
            this.level = level;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (frequency != item.frequency) return false;
            if (level != item.level) return false;
            if (bssid != null ? !bssid.equals(item.bssid) : item.bssid != null) return false;
            if (ssid != null ? !ssid.equals(item.ssid) : item.ssid != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = ssid != null ? ssid.hashCode() : 0;
            result = 31 * result + (bssid != null ? bssid.hashCode() : 0);
            result = 31 * result + frequency;
            result = 31 * result + level;
            return result;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "ssid='" + ssid + '\'' +
                    ", bssid='" + bssid + '\'' +
                    ", frequency=" + frequency +
                    ", level=" + level +
                    '}';
        }
    }

    /**
     * Items forming the environment
     */
    public final Item[] items;

    /**
     * Creates a new instance with the given values
     *
     * @param timestamp Time of the item
     * @param device    Device of the item
     * @param items     Items forming the environment
     */
    public WiFi(long timestamp, String device, Item[] items) {
        super(timestamp, device);
        this.items = items;
    }


    @Override
    public String getType() {
        return DataCommons.TYPE_WIFI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WiFi wiFi = (WiFi) o;

        if (!Arrays.equals(items, wiFi.items)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return items != null ? Arrays.hashCode(items) : 0;
    }

    @Override
    public String createSerializedForm() {
        return WiFiSerialization.WI_FI_SERIALIZATION.serialize(this);
    }
}
