package eu.liveandgov.wp1.sensor_collector.util;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.location.LocationProvider;
import android.telephony.NeighboringCellInfo;
import android.telephony.SignalStrength;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.DetectedActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lukashaertel on 05.12.2014.
 */
public class MoraConstants {
    public static String toConnectionResultString(int i) {
        switch (i) {
            case ConnectionResult.SUCCESS:
                return "success";
            case ConnectionResult.SERVICE_MISSING:
                return "service missing";
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "service version update required";
            case ConnectionResult.SERVICE_DISABLED:
                return "service disabled";
            case ConnectionResult.SIGN_IN_REQUIRED:
                return "sign in required";
            case ConnectionResult.INVALID_ACCOUNT:
                return "invalid account";
            case ConnectionResult.RESOLUTION_REQUIRED:
                return "resolution required";
            case ConnectionResult.NETWORK_ERROR:
                return "network error";
            case ConnectionResult.INTERNAL_ERROR:
                return "internal error";
            case ConnectionResult.SERVICE_INVALID:
                return "service invalid";
            case ConnectionResult.DEVELOPER_ERROR:
                return "developer error";
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return "license check failed";
            case ConnectionResult.CANCELED:
                return "canceled";
            case ConnectionResult.TIMEOUT:
                return "timeout";
            case ConnectionResult.INTERRUPTED:
                return "interrupted";
            case ConnectionResult.API_UNAVAILABLE:
                return "API unavailable";
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * <p>Utility method for converting the activity type into a readable string</p>
     *
     * @param activityType The integer describing the activity type, one of the constants
     *                     in {@link com.google.android.gms.location.DetectedActivity}
     * @return Returns a readable string
     */
    public static String toDetectedActivityString(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";

            default:
                return "unknown";
        }
    }

    /**
     * <p>Converts the location provider status to a string</p>
     *
     * @param status The status to convert
     * @return Returns a readable string
     */
    public static String toLocationListenerStatusString(int status) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                return "available";
            case LocationProvider.OUT_OF_SERVICE:
                return "out of service";
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                return "temporarily unavailable";
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * This TreeMap maps the integer representing the device major class to its name.
     */
    private static final Map<Integer, String> DEVICE_MAJOR_CLASS_MAP = new TreeMap<Integer, String>();
    /**
     * This TreeMap maps the integer representing the device class to its name.
     */
    private static final Map<Integer, String> DEVICE_CLASS_MAP = new TreeMap<Integer, String>();


    /**
     * Static constructor that fills the maps for string conversion
     */
    static {
        for (Field field : BluetoothClass.Device.Major.class.getFields()) {
            if ((field.getModifiers() & Modifier.STATIC) == 0) continue;
            if ((field.getModifiers() & Modifier.PUBLIC) == 0) continue;

            if (field.getType().isAssignableFrom(int.class)) {
                try {
                    // Insert inverse mapping
                    DEVICE_MAJOR_CLASS_MAP.put(field.getInt(null), field.getName().replace('_', ' ').toLowerCase());
                } catch (IllegalAccessException e) {
                    // Do nothing
                }
            }
        }

        // Apply the same pattern to the composed class
        for (Field field : BluetoothClass.Device.class.getFields()) {
            if ((field.getModifiers() & Modifier.STATIC) == 0) continue;
            if ((field.getModifiers() & Modifier.PUBLIC) == 0) continue;

            if (field.getType().isAssignableFrom(int.class)) {
                try {
                    // Insert inverse mapping
                    DEVICE_CLASS_MAP.put(field.getInt(null), field.getName().replace('_', ' ').toLowerCase());
                } catch (IllegalAccessException e) {
                    // Do nothing
                }
            }
        }
    }

    /**
     * <p>Gets the device major class name for a bluetooth class device major code</p>
     *
     * @param i The code to convert
     * @return Returns the representative name
     */
    public static String getDeviceMajorClassName(int i) {
        return DEVICE_MAJOR_CLASS_MAP.get(i);
    }

    /**
     * <p>Gets the device class name for a bluetooth class device code</p>
     *
     * @param i The The code to convert
     * @return Returns the representative name
     */
    public static String getDeviceClassName(int i) {
        return DEVICE_CLASS_MAP.get(i);
    }

    /**
     * <p>Gets a name for the bluetooth device bond state</p>
     *
     * @param i The state to convert
     * @return Returns the representative name
     */
    public static String getBondName(int i) {
        if (i == BluetoothDevice.BOND_NONE) {
            return "none";
        } else if (i == BluetoothDevice.BOND_BONDING) {
            return "bonding";
        } else if (i == BluetoothDevice.BOND_BONDED) {
            return "bonded";
        } else {
            return "unknown";
        }
    }

    /**
     * <p>Returns the Signal Strength in dBm</p>
     *
     * @param i The signal strength in TS27
     * @return Returns the signal strength in dBm
     */
    public static Integer convertTS27SignalStrength(int i) {
        if (i == 99) {
            return null;
        } else {
            return -113 + 2 * i;
        }
    }

    /**
     * <p>Gets the signal strength text for the given signal strength</p>
     *
     * @param signalStrength The signal strength to convert
     * @return Returns the signal strength text
     */
    public static String getSignalStrengthText(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            return String.format(Locale.ENGLISH, "gsm %d", convertTS27SignalStrength(signalStrength.getGsmSignalStrength()));
        } else {
            return String.format(Locale.ENGLISH, "other %d %d, %d %d", signalStrength.getCdmaDbm(), signalStrength.getCdmaEcio(), signalStrength.getEvdoDbm(), signalStrength.getEvdoEcio());
        }
    }

    /**
     * <p>Converts a cell identity code to a name</p>
     *
     * @param cid The CID
     * @param lac The LAC
     * @return Returns a composed cell identity text
     */
    public static String getCellIdentityText(int cid, int lac) {
        if (cid == NeighboringCellInfo.UNKNOWN_CID) {
            if (lac == NeighboringCellInfo.UNKNOWN_CID) {
                return "unknown";
            } else {
                return String.format(Locale.ENGLISH, "lac: %d", lac);
            }
        } else {
            if (lac == NeighboringCellInfo.UNKNOWN_CID) {
                return String.format(Locale.ENGLISH, "cid: %d", cid);
            } else {
                return String.format(Locale.ENGLISH, "cid: %d lac: %d", cid, lac);
            }
        }
    }
}
