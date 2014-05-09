package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.forwarding.Forwarding;
import eu.liveandgov.wp1.forwarding.Receiver;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.FIELD_TYPE;

/**
 * <p>Forwarding of a general item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public final class ItemForwarding implements Forwarding<Item> {
    /**
     * The one instance of the forwarding
     */
    public static final ItemForwarding ITEM_PACKAGING = new ItemForwarding();

    /**
     * Hidden constructor
     */
    protected ItemForwarding() {

    }

    @Override
    public void forward(Item item, Receiver target) {
        if (item instanceof GPS) {
            GPSForwarding.GPS_PACKAGING.forward((GPS) item, target);
            return;
        }

        if (item instanceof Motion) {
            MotionForwarding.MOTION_PACKAGING.forward((Motion) item, target);
            return;
        }

        if (item instanceof WiFi) {
            WiFiForwarding.WI_FI_PACKAGING.forward((WiFi) item, target);
            return;
        }

        if (item instanceof Bluetooth) {
            BluetoothForwarding.BLUETOOTH_PACKAGING.forward((Bluetooth) item, target);
            return;
        }

        if (item instanceof GSM) {
            GSMForwarding.GSM_PACKAGING.forward((GSM) item, target);
            return;
        }

        if (item instanceof Activity) {
            ActivityForwarding.ACTIVITY_PACKAGING.forward((Activity) item, target);
            return;
        }

        if (item instanceof GoogleActivity) {
            GoogleActivityForwarding.GOOGLE_ACTIVITY_PACKAGING.forward((GoogleActivity) item, target);
            return;
        }

        if (item instanceof Tag) {
            TagForwarding.TAG_PACKAGING.forward((Tag) item, target);
            return;
        }
        if (item instanceof Proximity) {
            ProximityForwarding.PROXIMITY_PACKAGING.forward((Proximity) item, target);
            return;
        }

        if (item instanceof Waiting) {
            WaitingForwarding.WAITING_PACKAGING.forward((Waiting) item, target);
            return;
        }

        throw new IllegalArgumentException();
    }
}
