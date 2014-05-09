package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.impl.Bluetooth;

/**
 * <p>Forwarding of the bluetooth item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class BluetoothForwarding extends UnsupportedForwarding<Bluetooth> {
    /**
     * The one instance of the forwarding
     */
    public static final BluetoothForwarding BLUETOOTH_PACKAGING = new BluetoothForwarding();

    /**
     * Hidden constructor
     */
    protected BluetoothForwarding() {
    }

}
