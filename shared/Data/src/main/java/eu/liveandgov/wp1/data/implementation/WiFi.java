package eu.liveandgov.wp1.data.implementation;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.annotations.Unit;

import java.util.List;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public final class WiFi extends Item<List<WiFi.WifiItem>> {
    public static boolean isWifi(String type) {
        return DataCommons.TYPE_WIFI.equals(type);
    }

    public static String assertIsWifi(String type) {
        assert isWifi(type);

        return type;
    }

    public static final class WifiItem {
        public final String ssid;

        public final String bssid;

        @Unit("MHz")
        public final int frequency;

        @Unit("dBm")
        public final int level;

        public WifiItem(String ssid, String bssid, @Unit("MHz") int frequency, @Unit("dBm") int level) {
            this.ssid = ssid;
            this.bssid = bssid;
            this.frequency = frequency;
            this.level = level;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WifiItem wifiItem = (WifiItem) o;

            if (frequency != wifiItem.frequency) return false;
            if (level != wifiItem.level) return false;
            if (bssid != null ? !bssid.equals(wifiItem.bssid) : wifiItem.bssid != null) return false;
            if (ssid != null ? !ssid.equals(wifiItem.ssid) : wifiItem.ssid != null) return false;

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
            return "WifiItem{" +
                    "ssid='" + ssid + '\'' +
                    ", bssid='" + bssid + '\'' +
                    ", frequency=" + frequency +
                    ", level=" + level +
                    '}';
        }
    }

    public WiFi(String type, Header header, List<WifiItem> wifiItems) {
        super(assertIsWifi(type), header, wifiItems);
    }
}
