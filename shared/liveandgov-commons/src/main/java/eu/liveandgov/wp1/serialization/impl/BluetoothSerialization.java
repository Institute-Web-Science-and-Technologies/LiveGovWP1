package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.impl.Bluetooth;
import eu.liveandgov.wp1.serialization.Wrapper;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class BluetoothSerialization extends AbstractSerialization<Bluetooth> {
    public static final BluetoothSerialization BLUETOOTH_SERIALIZATION = new BluetoothSerialization();

    private BluetoothSerialization() {
    }


    @Override
    protected void serializeRest(StringBuilder stringBuilder, Bluetooth bluetooth) {
        if (bluetooth.items.length > 0) {
            appendBluetoothItem(bluetooth, stringBuilder, 0);

            for (int i = 1; i < bluetooth.items.length; i++) {
                stringBuilder.append(SEMICOLON);

                appendBluetoothItem(bluetooth, stringBuilder, i);
            }
        }
    }

    private static void appendBluetoothItem(Bluetooth bluetooth, StringBuilder stringBuilder, int i) {
        final Bluetooth.Item item = bluetooth.items[i];

        appendString(stringBuilder, item.address);
        stringBuilder.append(SLASH);
        stringBuilder.append(item.deviceMajorClass);
        stringBuilder.append(SLASH);
        stringBuilder.append(item.deviceMinorClass);
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(item.bondState));
        stringBuilder.append(SLASH);
        stringBuilder.append(escape(item.name));
        stringBuilder.append(SLASH);
        stringBuilder.append(item.rssi != null ? item.rssi : "");
    }

    @Override
    protected Bluetooth deSerializeRest(String type, long timestamp, String device, Scanner scanner) {
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final List<Bluetooth.Item> itemList = new ArrayList<Bluetooth.Item>();
        while (hasNextString(scanner)) {
            final String address = nextString(scanner);
            final String deviceMajorClass = nextString(scanner);
            final String deviceMinorClass = nextString(scanner);
            final Bluetooth.BondState bondState = fromText(Bluetooth.BondState.class, nextString(scanner));
            final String name = nextString(scanner);
            final Short rssi = scanner.hasNextShort() ? scanner.nextShort() : null;

            itemList.add(new Bluetooth.Item(address, deviceMajorClass, deviceMinorClass, bondState, name, rssi));
        }

        return new Bluetooth(timestamp, device, itemList.toArray(Bluetooth.Item.EMPTY_ARRAY));
    }
}
