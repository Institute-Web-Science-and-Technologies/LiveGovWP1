package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.impl.Bluetooth;

/**
 * <p>Packaging of the bluetooth item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class BluetoothPackaging extends UnsupportedPackaging<Bluetooth> {
    /**
     * The one instance of the packaging
     */
    public static final BluetoothPackaging BLUETOOTH_PACKAGING = new BluetoothPackaging();

    /**
     * Hidden constructor
     */
    protected BluetoothPackaging() {
    }

}
