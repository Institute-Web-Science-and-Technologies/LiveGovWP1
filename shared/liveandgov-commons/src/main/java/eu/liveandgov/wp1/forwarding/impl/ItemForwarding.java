package eu.liveandgov.wp1.forwarding.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.forwarding.Forwarding;
import eu.liveandgov.wp1.forwarding.ForwardingCommons;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;

/**
 * <p>Forwarding of a general item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public final class ItemForwarding implements Forwarding<Item> {
    /**
     * The one instance of the forwarding
     */
    public static final ItemForwarding ITEM_FORWARDING = new ItemForwarding();

    /**
     * Hidden constructor
     */
    protected ItemForwarding() {

    }

    @Override
    public void forward(Item item, Receiver target) {
        if (item instanceof GPS) {
            GPSForwarding.GPS_FORWARDING.forward((GPS) item, target);
            return;
        }

        if (item instanceof Motion) {
            MotionForwarding.MOTION_FORWARDING.forward((Motion) item, target);
            return;
        }

        if (item instanceof WiFi) {
            WiFiForwarding.WI_FI_FORWARDING.forward((WiFi) item, target);
            return;
        }

        if (item instanceof Bluetooth) {
            BluetoothForwarding.BLUETOOTH_FORWARDING.forward((Bluetooth) item, target);
            return;
        }

        if (item instanceof GSM) {
            GSMForwarding.GSM_FORWARDING.forward((GSM) item, target);
            return;
        }

        if (item instanceof Activity) {
            ActivityForwarding.ACTIVITY_FORWARDING.forward((Activity) item, target);
            return;
        }

        if (item instanceof GoogleActivity) {
            GoogleActivityForwarding.GOOGLE_ACTIVITY_FORWARDING.forward((GoogleActivity) item, target);
            return;
        }

        if (item instanceof Tag) {
            TagForwarding.TAG_FORWARDING.forward((Tag) item, target);
            return;
        }
        if (item instanceof Proximity) {
            ProximityForwarding.PROXIMITY_FORWARDING.forward((Proximity) item, target);
            return;
        }

        if (item instanceof Waiting) {
            WaitingForwarding.WAITING_FORWARDING.forward((Waiting) item, target);
            return;
        }

        throw new IllegalArgumentException();
    }

    @Override
    public Item unForward(Provider source) {
        String type = (String) source.provide(ForwardingCommons.FIELD_TYPE);

        if (DataCommons.TYPE_GPS.equals(type)) {
            return GPSForwarding.GPS_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_ACCELEROMETER.equals(type)
                | DataCommons.TYPE_LINEAR_ACCELERATION.equals(type)
                | DataCommons.TYPE_GRAVITY.equals(type)
                | DataCommons.TYPE_GYROSCOPE.equals(type)
                | DataCommons.TYPE_MAGNETOMETER.equals(type)
                | DataCommons.TYPE_ROTATION.equals(type)) {
            return MotionForwarding.MOTION_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_WIFI.equals(type)) {
            return WiFiForwarding.WI_FI_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_BLUETOOTH.equals(type)) {
            return BluetoothForwarding.BLUETOOTH_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_GSM.equals(type)) {
            return GSMForwarding.GSM_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_ACTIVITY.equals(type)) {
            return ActivityForwarding.ACTIVITY_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_GOOGLE_ACTIVITY.equals(type)) {
            return GoogleActivityForwarding.GOOGLE_ACTIVITY_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_TAG.equals(type)) {
            return TagForwarding.TAG_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_PROXIMITY.equals(type)) {
            return ProximityForwarding.PROXIMITY_FORWARDING.unForward(source);
        }

        if (DataCommons.TYPE_WAITING.equals(type)) {
            return WaitingForwarding.WAITING_FORWARDING.unForward(source);
        }

        throw new IllegalArgumentException();
    }
}
