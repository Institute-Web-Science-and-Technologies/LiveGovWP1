package eu.liveandgov.wp1.data.impl;

import eu.liveandgov.wp1.data.AbstractItem;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.annotations.Unit;

import java.util.Arrays;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class WiFi extends AbstractItem {
    public static final class Item {
        public static final Item[] EMPTY_ARRAY = new Item[0];

        public final String ssid;

        public final String bssid;

        @Unit("MHz")
        public final int frequency;

        @Unit("dBm")
        public final int level;

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

    public final Item[] items;

    public WiFi(long timestamp, String device, Item[] items) {
        super(timestamp, device);
        this.items = items;
    }

    public WiFi(eu.liveandgov.wp1.data.Item header, Item[] items) {
        super(header);
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
    public String toString() {
        return "WiFi{" +
                "items=" + Arrays.toString(items) +
                '}';
    }
}
