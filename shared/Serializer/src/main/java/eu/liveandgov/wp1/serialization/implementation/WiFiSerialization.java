package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.implementation.WiFi;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class WiFiSerialization extends Wrapper<WiFi, Item<String>> {
    public static final WiFiSerialization WI_FI_SERIALIZATION = new WiFiSerialization();

    private WiFiSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(WiFi wiFi) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (wiFi.data.size() > 0) {
            appendWifiItem(wiFi, stringBuilder, 0);

            for (int i = 1; i < wiFi.data.size(); i++) {
                stringBuilder.append(SEMICOLON);

                appendWifiItem(wiFi, stringBuilder, i);
            }
        }

        return new Item<String>(wiFi.type, wiFi.header, stringBuilder.toString());
    }

    private static void appendWifiItem(WiFi wiFi, StringBuilder stringBuilder, int i) {
        final WiFi.WifiItem wifiItem = wiFi.data.get(i);

        appendString(stringBuilder, wifiItem.ssid);
        stringBuilder.append(SLASH);
        appendString(stringBuilder, wifiItem.bssid);
        stringBuilder.append(SLASH);
        stringBuilder.append(wifiItem.frequency);
        stringBuilder.append(SLASH);
        stringBuilder.append(wifiItem.level);
    }

    @Override
    protected WiFi invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final List<WiFi.WifiItem> itemList = new ArrayList<WiFi.WifiItem>();
        while (hasNextString(scanner)) {
            final String ssid = nextString(scanner);
            final String bssid = nextString(scanner);
            final int frequency = scanner.nextInt();
            final int level = scanner.nextInt();

            itemList.add(new WiFi.WifiItem(ssid, bssid, frequency, level));
        }

        return new WiFi(stringItem.type, stringItem.header, itemList);
    }
}
