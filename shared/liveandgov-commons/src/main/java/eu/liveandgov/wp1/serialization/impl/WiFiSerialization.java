package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Arbitrary;
import eu.liveandgov.wp1.data.impl.WiFi;

import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.serialization.SerializationCommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class WiFiSerialization extends Wrapper<WiFi, Arbitrary> {
    public static final WiFiSerialization WI_FI_SERIALIZATION = new WiFiSerialization();

    private WiFiSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Arbitrary transform(WiFi wiFi) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (wiFi.items.length > 0) {
            appendWifiItem(wiFi, stringBuilder, 0);

            for (int i = 1; i < wiFi.items.length; i++) {
                stringBuilder.append(SerializationCommons.SEMICOLON);

                appendWifiItem(wiFi, stringBuilder, i);
            }
        }

        return new Arbitrary(wiFi, wiFi.getType(), stringBuilder.toString());
    }

    private static void appendWifiItem(WiFi wiFi, StringBuilder stringBuilder, int i) {
        final WiFi.Item item = wiFi.items[i];

        SerializationCommons.appendString(stringBuilder, item.ssid);
        stringBuilder.append(SerializationCommons.SLASH);
        SerializationCommons.appendString(stringBuilder, item.bssid);
        stringBuilder.append(SerializationCommons.SLASH);
        stringBuilder.append(item.frequency);
        stringBuilder.append(SerializationCommons.SLASH);
        stringBuilder.append(item.level);
    }

    @Override
    protected WiFi invertTransform(Arbitrary item) {
        final Scanner scanner = new Scanner(item.value);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SerializationCommons.SLASH_SEMICOLON_SEPARATED);

        final List<WiFi.Item> itemList = new ArrayList<WiFi.Item>();
        while (SerializationCommons.hasNextString(scanner)) {
            final String ssid = SerializationCommons.nextString(scanner);
            final String bssid = SerializationCommons.nextString(scanner);
            final int frequency = scanner.nextInt();
            final int level = scanner.nextInt();

            itemList.add(new WiFi.Item(ssid, bssid, frequency, level));
        }

        return new WiFi(item, itemList.toArray(WiFi.Item.EMPTY_ARRAY));
    }
}
