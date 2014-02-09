package eu.liveandgov.wp1.serialization.implementation;

import eu.liveandgov.wp1.data.implementation.Bluetooth;
import eu.liveandgov.wp1.data.Item;

import static eu.liveandgov.wp1.serialization.SerializationCommons.*;

import eu.liveandgov.wp1.serialization.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Lukas Härtel on 08.02.14.
 */
public class BluetoothSerialization extends Wrapper<Bluetooth, Item<String>> {
    public static final BluetoothSerialization BLUETOOTH_SERIALIZATION = new BluetoothSerialization();

    private BluetoothSerialization() {
        super(BasicSerialization.BASIC_SERIALIZATION);
    }

    @Override
    protected Item<String> transform(Bluetooth bluetooth) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (bluetooth.data.size() > 0) {
            appendBluetoothItem(bluetooth, stringBuilder, 0);

            for (int i = 1; i < bluetooth.data.size(); i++) {
                stringBuilder.append(SEMICOLON);

                appendBluetoothItem(bluetooth, stringBuilder, i);
            }
        }

        return new Item<String>(bluetooth.type, bluetooth.header, stringBuilder.toString());
    }

    private static void appendBluetoothItem(Bluetooth bluetooth, StringBuilder stringBuilder, int i) {
        final Bluetooth.BluetoothItem bluetoothItem = bluetooth.data.get(i);

        appendString(stringBuilder, bluetoothItem.address);
        stringBuilder.append(SLASH);
        stringBuilder.append(bluetoothItem.deviceMajorClass);
        stringBuilder.append(SLASH);
        stringBuilder.append(bluetoothItem.deviceMinorClass);
        stringBuilder.append(SLASH);
        stringBuilder.append(toText(bluetoothItem.bondState));
        stringBuilder.append(SLASH);
        stringBuilder.append(escape(bluetoothItem.name));
        stringBuilder.append(SLASH);
        stringBuilder.append(bluetoothItem.rssi != null ? bluetoothItem.rssi : "");
    }

    @Override
    protected Bluetooth invertTransform(Item<String> stringItem) {
        final Scanner scanner = new Scanner(stringItem.data);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(SLASH_SEMICOLON_SEPARATED);

        final List<Bluetooth.BluetoothItem> itemList = new ArrayList<Bluetooth.BluetoothItem>();
        while (hasNextString(scanner)) {
            final String address = nextString(scanner);
            final String deviceMajorClass = nextString(scanner);
            final String deviceMinorClass = nextString(scanner);
            final Bluetooth.BondState bondState = fromText(Bluetooth.BondState.class, nextString(scanner));
            final String name = nextString(scanner);
            final Short rssi = scanner.hasNextShort() ? scanner.nextShort() : null;

            itemList.add(new Bluetooth.BluetoothItem(address, deviceMajorClass, deviceMinorClass, bondState, name, rssi));
        }

        return new Bluetooth(stringItem.type, stringItem.header, itemList);
    }
}
