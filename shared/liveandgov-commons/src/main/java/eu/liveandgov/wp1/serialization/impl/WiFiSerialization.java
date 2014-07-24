package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.WiFi;
import eu.liveandgov.wp1.serialization.SerializationCommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Serialization of the WiFi item</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class WiFiSerialization extends AbstractSerialization<WiFi> {
    /**
     * The one instance of the serialization
     */
    public static final WiFiSerialization WI_FI_SERIALIZATION = new WiFiSerialization();

    /**
     * Hidden constructor
     */
    protected WiFiSerialization() {
    }

    @Override
    protected void serializeRest(StringBuilder stringBuilder, WiFi wiFi) {
        if (wiFi.items.length > 0) {
            appendWifiItem(wiFi, stringBuilder, 0);

            for (int i = 1; i < wiFi.items.length; i++) {
                stringBuilder.append(SerializationCommons.SEMICOLON);

                appendWifiItem(wiFi, stringBuilder, i);
            }
        }
    }

    /**
     * Writes one WiFi sub-item to the string builder
     *
     * @param wiFi          The WiFi item
     * @param stringBuilder The target string builder
     * @param i             The index of the item to write
     */
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
    protected WiFi deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SerializationCommons.SLASH_SEMICOLON_SEPARATED);

        final List<WiFi.Item> itemList = new ArrayList<WiFi.Item>();
        while (SerializationCommons.hasNextString(scanner)) {
            final String ssid = SerializationCommons.nextString(scanner);
            final String bssid = SerializationCommons.nextString(scanner);
            final int frequency = scanner.nextInt();
            final int level = scanner.nextInt();

            itemList.add(new WiFi.Item(ssid, bssid, frequency, level));
        }

        return new WiFi(timestamp, device, itemList.toArray(new WiFi.Item[itemList.size()]));
    }
}
