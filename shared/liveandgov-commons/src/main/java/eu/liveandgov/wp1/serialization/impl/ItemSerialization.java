package eu.liveandgov.wp1.serialization.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.serialization.Serialization;

import java.util.Locale;
import java.util.Scanner;

import static eu.liveandgov.wp1.serialization.SerializationCommons.COMMA_SEPARATED;

/**
 * <p>Serialization of a general item</p>
 * Created by Lukas Härtel on 09.02.14.
 */
public final class ItemSerialization implements Serialization<Item> {
    /**
     * The one instance of the serialization
     */
    public static final ItemSerialization ITEM_SERIALIZATION = new ItemSerialization();

    /**
     * Hidden constructor
     */
    protected ItemSerialization() {

    }

    @Override
    public String serialize(Item item) {
        if (item instanceof GPS) {
            return GPSSerialization.GPS_SERIALIZATION.serialize((GPS) item);
        }

        if (item instanceof Motion) {
            return MotionSerialization.MOTION_SERIALIZATION.serialize((Motion) item);
        }

        if (item instanceof WiFi) {
            return WiFiSerialization.WI_FI_SERIALIZATION.serialize((WiFi) item);
        }

        if (item instanceof Bluetooth) {
            return BluetoothSerialization.BLUETOOTH_SERIALIZATION.serialize((Bluetooth) item);
        }

        if (item instanceof GSM) {
            return GSMSerialization.GSM_SERIALIZATION.serialize((GSM) item);
        }

        if (item instanceof Activity) {
            return ActivitySerialization.ACTIVITY_SERIALIZATION.serialize((Activity) item);
        }

        if (item instanceof GoogleActivity) {
            return GoogleActivitySerialization.GOOGLE_ACTIVITY_SERIALIZATION.serialize((GoogleActivity) item);
        }

        if (item instanceof Tag) {
            return TagSerialization.TAG_SERIALIZATION.serialize((Tag) item);
        }
        if (item instanceof Proximity) {
            return ProximitySerialization.PROXIMITY_SERIALIZATION.serialize((Proximity) item);
        }

        if (item instanceof Waiting) {
            return WaitingSerialization.WAITING_SERIALIZATION.serialize((Waiting) item);
        }

        if (item instanceof Velocity) {
            return VelocitySerialization.VELOCITY_SERIALIZATION.serialize((Velocity) item);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Item deSerialize(String string) {
        final Scanner scanner = new Scanner(string);
        scanner.useLocale(Locale.ENGLISH);
        scanner.useDelimiter(COMMA_SEPARATED);

        final String type = scanner.next();

        if (DataCommons.TYPE_GPS.equals(type)) {
            return GPSSerialization.GPS_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_ACCELEROMETER.equals(type)
                | DataCommons.TYPE_LINEAR_ACCELERATION.equals(type)
                | DataCommons.TYPE_GRAVITY.equals(type)
                | DataCommons.TYPE_GYROSCOPE.equals(type)
                | DataCommons.TYPE_MAGNETOMETER.equals(type)
                | DataCommons.TYPE_ROTATION.equals(type)) {
            return MotionSerialization.MOTION_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_WIFI.equals(type)) {
            return WiFiSerialization.WI_FI_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_BLUETOOTH.equals(type)) {
            return BluetoothSerialization.BLUETOOTH_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_GSM.equals(type)) {
            return GSMSerialization.GSM_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_ACTIVITY.equals(type)) {
            return ActivitySerialization.ACTIVITY_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_GOOGLE_ACTIVITY.equals(type)) {
            return GoogleActivitySerialization.GOOGLE_ACTIVITY_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_TAG.equals(type)) {
            return TagSerialization.TAG_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_PROXIMITY.equals(type)) {
            return ProximitySerialization.PROXIMITY_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_WAITING.equals(type)) {
            return WaitingSerialization.WAITING_SERIALIZATION.deSerialize(string);
        }

        if (DataCommons.TYPE_VELOCITY.equals(type)) {
            return VelocitySerialization.VELOCITY_SERIALIZATION.deSerialize(string);
        }


        throw new IllegalArgumentException("Illegal format on: " + string);
    }
}
