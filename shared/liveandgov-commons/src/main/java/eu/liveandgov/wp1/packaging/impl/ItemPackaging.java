package eu.liveandgov.wp1.packaging.impl;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.impl.*;
import eu.liveandgov.wp1.packaging.Packaging;

import java.util.Map;

import static eu.liveandgov.wp1.packaging.PackagingCommons.FIELD_TYPE;

/**
 * <p>Packaging of a general item</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public final class ItemPackaging implements Packaging<Item> {
    /**
     * The one instance of the packaging
     */
    public static final ItemPackaging ITEM_PACKAGING = new ItemPackaging();

    /**
     * Hidden constructor
     */
    protected ItemPackaging() {

    }

    @Override
    public Map<String, ?> pack(Item item) {
        if (item instanceof GPS) {
            return GPSPackaging.GPS_PACKAGING.pack((GPS) item);
        }

        if (item instanceof Motion) {
            return MotionPackaging.MOTION_PACKAGING.pack((Motion) item);
        }

        if (item instanceof WiFi) {
            return WiFiPackaging.WI_FI_PACKAGING.pack((WiFi) item);
        }

        if (item instanceof Bluetooth) {
            return BluetoothPackaging.BLUETOOTH_PACKAGING.pack((Bluetooth) item);
        }

        if (item instanceof GSM) {
            return GSMPackaging.GSM_PACKAGING.pack((GSM) item);
        }

        if (item instanceof Activity) {
            return ActivityPackaging.ACTIVITY_PACKAGING.pack((Activity) item);
        }

        if (item instanceof GoogleActivity) {
            return GoogleActivityPackaging.GOOGLE_ACTIVITY_PACKAGING.pack((GoogleActivity) item);
        }

        if (item instanceof Tag) {
            return TagPackaging.TAG_PACKAGING.pack((Tag) item);
        }
        if (item instanceof Proximity) {
            return ProximityPackaging.PROXIMITY_PACKAGING.pack((Proximity) item);
        }

        if (item instanceof Waiting) {
            return WaitingPackaging.WAITING_PACKAGING.pack((Waiting) item);
        }

        throw new IllegalArgumentException();
    }

    @Override
    public Item unPack(Map<String, ?> map) {
        final String type = (String) map.get(FIELD_TYPE);

        if (DataCommons.TYPE_GPS.equals(type)) {
            return GPSPackaging.GPS_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_ACCELEROMETER.equals(type)
                | DataCommons.TYPE_LINEAR_ACCELERATION.equals(type)
                | DataCommons.TYPE_GRAVITY.equals(type)
                | DataCommons.TYPE_GYROSCOPE.equals(type)
                | DataCommons.TYPE_MAGNETOMETER.equals(type)
                | DataCommons.TYPE_ROTATION.equals(type)) {
            return MotionPackaging.MOTION_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_WIFI.equals(type)) {
            return WiFiPackaging.WI_FI_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_BLUETOOTH.equals(type)) {
            return BluetoothPackaging.BLUETOOTH_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_GSM.equals(type)) {
            return GSMPackaging.GSM_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_ACTIVITY.equals(type)) {
            return ActivityPackaging.ACTIVITY_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_GOOGLE_ACTIVITY.equals(type)) {
            return GoogleActivityPackaging.GOOGLE_ACTIVITY_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_TAG.equals(type)) {
            return TagPackaging.TAG_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_PROXIMITY.equals(type)) {
            return ProximityPackaging.PROXIMITY_PACKAGING.unPack(map);
        }

        if (DataCommons.TYPE_WAITING.equals(type)) {
            return WaitingPackaging.WAITING_PACKAGING.unPack(map);
        }

        throw new IllegalArgumentException();
    }
}
