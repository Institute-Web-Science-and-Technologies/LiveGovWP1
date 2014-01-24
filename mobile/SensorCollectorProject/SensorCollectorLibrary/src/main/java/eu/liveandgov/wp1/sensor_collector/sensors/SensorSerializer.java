package eu.liveandgov.wp1.sensor_collector.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.telephony.NeighboringCellInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;


import org.apache.commons.lang.StringEscapeUtils;

import java.util.Locale;

import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers.TelephonyHolder;
import eu.liveandgov.wp1.sensor_collector.sensors.sensor_value_objects.GpsSensorValue;

import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_ACCELEROMETER;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_BLUETOOTH;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_ERROR;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_GOOGLE_ACTIVITY;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_GPS;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_GRAVITY;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_GSM;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_GYROSCOPE;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_LINEAR_ACCELERATION;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_MAGNETOMETER;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_ROTATION;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_TAG;
import static eu.liveandgov.wp1.sensor_collector.configuration.SsfFileFormat.SSF_WIFI;

/**
 * Converts sensor events into the ssf Format.
 * <p/>
 * Created by hartmann on 9/15/13.
 */
public class SensorSerializer {
    /**
     * Baseclass for SSF conversion helpers
     */
    public static abstract class UtilityConversion<T> {
        /**
         * Uses this conversion to convert from t to a string
         */
        public abstract String toSSF(String type, long timestamp, String device, T t);

        /**
         * Delegates the calculation of timestamp and device for {@link #toSSF(String, long, String, T)}
         */
        public String toSSFDefault(String type, T t) {
            return toSSF(type, System.currentTimeMillis(), GlobalContext.getUserId(), t);
        }
    }

    /**
     * Baseclass for SSF conversions
     */
    public static abstract class Conversion<T> {
        /**
         * Uses this conversion to convert from t to a string
         */
        public abstract String toSSF(long timestamp, String device, T t);

        /**
         * Delegates the calculation of timestamp and device for {@link #toSSF(long, String, T)}
         */
        public String toSSFDefault(T t) {
            return toSSF(System.currentTimeMillis(), GlobalContext.getUserId(), t);
        }
    }


    public static MotionSensorValue parseMotionEvent(String event) {
        MotionSensorValue newEvent = new MotionSensorValue();

        // Meta data
        String[] temp = event.split(",");
        newEvent.type = temp[0];
        newEvent.time = Long.parseLong(temp[1]);
        newEvent.id = temp[2];

        // Values
        String[] values = temp[3].split(" ");
        newEvent.x = Float.parseFloat(values[0]);
        newEvent.y = Float.parseFloat(values[1]);
        newEvent.z = Float.parseFloat(values[2]);

        return newEvent;
    }

    public static GpsSensorValue parseGpsEvent(String event) {
        GpsSensorValue newEvent = new GpsSensorValue();

        // Meta data
        String[] temp = event.split(",");
        newEvent.type = temp[0];
        newEvent.time = Long.parseLong(temp[1]);
        newEvent.id = temp[2];

        // Values
        String[] values = temp[3].split(" ");
        newEvent.lat = Float.parseFloat(values[0]);
        newEvent.lon = Float.parseFloat(values[1]);
        newEvent.alt = Float.parseFloat(values[2]);

        return newEvent;
    }

    /**
     * Escapes the device-string
     */
    private static String escapeDevice(String d) {
        return d.replace(',', ' ').replace('\r', ' ').replace('\n',' ');
    }

    /**
     * Escapes a string and puts it into quotes
     */
    private static String escape(String s) {
        if (s == null) return "";
        return '"' + StringEscapeUtils.escapeJava(s) + '"';
    }

    /**
     * Creates a string builder and initializes it with the default pattern
     */
    private static StringBuilder createHead(String type, long timestamp, String device) {
        final StringBuilder builder = new StringBuilder(type);
        builder.append(',');
        builder.append(timestamp);
        builder.append(',');
        builder.append(escapeDevice(device));

        return builder;
    }

    /**
     * Utility class fro converting SSF entries with a string-tail
     */
    public static final UtilityConversion<String> escapedString = new UtilityConversion<String>() {
        @Override
        public String toSSF(String type, long timestamp, String device, String s) {
            // Create head and separate from tail
            final StringBuilder builder = createHead(type, timestamp, device);
            builder.append(',');

            // Append error message
            builder.append(s);

            // Return result
            return builder.toString();
        }
    };

    /**
     * Utility class for converting SSF entries with an string-tail
     */
    public static final UtilityConversion<String> unescapedString = new UtilityConversion<String>() {
        @Override
        public String toSSF(String type, long timestamp, String device, String s) {
            // Escape and redelegate
            return escapedString.toSSF(type, timestamp, device, escape(s));
        }
    };

    /**
     * Utility class fro converting SSF entries with a float[]-tail
     */
    public static final UtilityConversion<float[]> floats = new UtilityConversion<float[]>() {
        @Override
        public String toSSF(String type, long timestamp, String device, float... floats) {
            // Create head and separate from tail
            final StringBuilder builder = createHead(type, timestamp, device);
            builder.append(',');

            // Append all floats separated
            if (floats.length > 0) {
                builder.append(floats[0]);
                for (int i = 1; i < floats.length; i++) {
                    builder.append(' ');
                    builder.append(floats[i]);
                }
            }

            return builder.toString();
        }
    };

    /**
     * Utility class fro converting SSF entries with a double[]-tail
     */
    public static final UtilityConversion<double[]> doubles = new UtilityConversion<double[]>() {
        @Override
        public String toSSF(String type, long timestamp, String device, double... doubles) {
            // Create head and separate from tail
            final StringBuilder builder = createHead(type, timestamp, device);
            builder.append(',');

            // Append all doubles separated
            if (doubles.length > 0) {
                builder.append(doubles[0]);
                for (int i = 1; i < doubles.length; i++) {
                    builder.append(' ');
                    builder.append(doubles[i]);
                }
            }

            return builder.toString();
        }
    };

    /**
     * Utility class fro converting SSF entries with a Object[]-tail
     */
    public static final UtilityConversion<Object[]> objects = new UtilityConversion<Object[]>() {
        @Override
        public String toSSF(String type, long timestamp, String device, Object... objects) {
            // Create head and separate from tail
            final StringBuilder builder = createHead(type, timestamp, device);
            builder.append(',');

            // Append all objects separated
            if (objects.length > 0) {
                builder.append(objects[0]);
                for (int i = 1; i < objects.length; i++) {
                    builder.append(' ');
                    builder.append(objects[i]);
                }
            }

            return builder.toString();
        }
    };


    /**
     * Converts a sensor eventx into the SSF format
     */
    public static final Conversion<SensorEvent> sensorEvent = new Conversion<SensorEvent>() {
        /**
         * Correction value for sensor timestamps
         */
        private final long timestampCorrectionMs;

        /**
         * Initializers checks build-version and sets the appropriate timestamp correction
         */ {
            // If build-version is above jelly-bean mr1 (17), timestamps of the sensors are already in
            // utc, otherwise convert by rebasing them on the uptime
            //
            // We comute a global correction based on the fact, that currentTimeMillis is in UTC
            // and nanoTime is in uptime.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                timestampCorrectionMs = (long) (System.currentTimeMillis() - (System.nanoTime() / 1E6));
            } else {
                timestampCorrectionMs = 0;
            }
        }

        @Override
        public String toSSF(long timestamp, String device, SensorEvent sensorEvent) {
            final String type;
            switch (sensorEvent.sensor.getType()) {
                // Assign type with correct representation
                case Sensor.TYPE_ACCELEROMETER:
                    type = SSF_ACCELEROMETER;
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    type = SSF_LINEAR_ACCELERATION;
                    break;
                case Sensor.TYPE_GRAVITY:
                    type = SSF_GRAVITY;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    type = SSF_GYROSCOPE;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    type = SSF_MAGNETOMETER;
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    type = SSF_ROTATION;
                    break;

                // Or return a error string
                default:
                    return unescapedString.toSSF(SSF_ERROR, timestamp, device, "Unknown sensor");
            }

            // Replace timestamp with real value
            timestamp = (long) (sensorEvent.timestamp / 1E6) + timestampCorrectionMs;

            // Return by float conversion
            return floats.toSSF(type, timestamp, device, sensorEvent.values);
        }
    };

    /**
     * Converts WiFi scan-results into the SSF format
     */
    public static final Conversion<Iterable<? extends ScanResult>> scanResults = new Conversion<Iterable<? extends ScanResult>>() {
        @Override
        public String toSSF(long timestamp, String device, Iterable<? extends ScanResult> scanResults) {
            // Create head and separate from tail
            final StringBuilder builder = createHead(SSF_WIFI, timestamp, device);
            builder.append(',');

            boolean separate = false;
            for (ScanResult scanResult : scanResults) {
                if (separate) {
                    // Separate entries of the scan result list by semicolon
                    builder.append(';');
                }

                // Write each scan result as a tuple of Escaped SSID/Escaped BSSID/Frequency in MHz/Level in dBm
                builder.append(escape(scanResult.SSID));
                builder.append('/');
                builder.append(escape(scanResult.BSSID));
                builder.append('/');
                builder.append(scanResult.frequency);
                builder.append('/');
                builder.append(scanResult.level);

                separate = true;
            }

            return builder.toString();
        }
    };

    /**
     * Converts a bluetooth intermediate string into the SSF format
     */
    public static final Conversion<String> bluetoothIntermediate = new Conversion<String>() {
        @Override
        public String toSSF(long timestamp, String device, String s) {
            // Redelegate
            return escapedString.toSSF(SSF_BLUETOOTH, timestamp, device, s);
        }
    };

    /**
     * Converts a phone state into the SSF format
     */
    public static final Conversion<TelephonyHolder.PhoneState> phoneState = new Conversion<TelephonyHolder.PhoneState>() {

        public String getStateName(int i) {
            if (i == ServiceState.STATE_EMERGENCY_ONLY) {
                return "emergency only";
            } else if (i == ServiceState.STATE_IN_SERVICE) {
                return "in service";
            } else if (i == ServiceState.STATE_OUT_OF_SERVICE) {
                return "out of service";
            } else if (i == ServiceState.STATE_POWER_OFF) {
                return "power off";
            } else {
                return "unknown";
            }
        }

        public String getRoamingText(boolean isRoaming) {
            // TODO: Proper opposite of roaming??!
            return isRoaming ? "roaming" : "not roaming";
        }

        public String getManualModeText(boolean isManualMode) {
            return isManualMode ? "manual carrier" : "automatic carrier";
        }

        public String getSignalStrengthText(SignalStrength signalStrength) {
            if (signalStrength.isGsm()) {
                return String.format(Locale.ENGLISH, "gsm %d", convertTS27SignalStrength(signalStrength.getGsmSignalStrength()));
            } else {
                return String.format(Locale.ENGLISH, "other %d %d, %d %d", signalStrength.getCdmaDbm(), signalStrength.getCdmaEcio(), signalStrength.getEvdoDbm(), signalStrength.getEvdoEcio());
            }
        }

        /**
         * Returns the Signal Strength in dBm
         */
        public Integer convertTS27SignalStrength(int i) {
            if (i == 99) {
                return null;
            } else {
                return -113 + 2 * i;
            }
        }

        public String getNetworkTypeText(int nt) {
            switch (nt) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
            }

            return "other";
        }

        public String getTS27SignalStrengthText(int i) {
            if (i == 99) {
                return "unknown";
            } else {
                return String.format(Locale.ENGLISH, "%d", convertTS27SignalStrength(i));
            }
        }

        public String getIdentityText(int cid, int lac) {
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

        @Override
        public String toSSF(long timestamp, String device, TelephonyHolder.PhoneState phoneState) {
            // Create head and separate from tail
            final StringBuilder builder = createHead(SSF_GSM, timestamp, device);
            builder.append(',');

            // Write phone status as Service State/Roaming State/Manual Selection State/Operator Name/Phone Signal Strength
            builder.append(getStateName(phoneState.getServiceState().getState()));
            builder.append('/');
            builder.append(getRoamingText(phoneState.getServiceState().getRoaming()));
            builder.append('/');
            builder.append(getManualModeText(phoneState.getServiceState().getIsManualSelection()));
            builder.append('/');
            builder.append(escape(phoneState.getServiceState().getOperatorAlphaLong()));
            builder.append('/');
            builder.append(escape(getSignalStrengthText(phoneState.getSignalStrength())));

            // Separate prefix from cells with colon
            builder.append(':');

            // Write each cell info as a tuple of Escaped Identity/Network Type/Signal Strength in dBm
            boolean separate = false;
            for (NeighboringCellInfo neighboringCellInfo : phoneState.getNeighboringCellInfos()) {
                if (separate) {
                    // Separate entries of the scan result list by semicolon
                    builder.append(';');
                }

                builder.append(escape(getIdentityText(neighboringCellInfo.getCid(), neighboringCellInfo.getLac())));
                builder.append('/');
                builder.append(escape(getNetworkTypeText(neighboringCellInfo.getNetworkType())));
                builder.append('/');
                builder.append(getTS27SignalStrengthText(neighboringCellInfo.getRssi()));

                separate = true;
            }

            return builder.toString();
        }
    };

    /**
     * Converts a location into SSF format
     */
    public static final Conversion<Location> location = new Conversion<Location>() {
        @Override
        public String toSSF(long timestamp, String device, Location location) {
            // Redelegate componentially
            return doubles.toSSF(SSF_GPS, timestamp, device, new double[]{location.getLatitude(), location.getLongitude(), location.getAltitude()});
        }
    };

    /**
     * Converts a tag into SSF format
     */
    public static final Conversion<String> tag = new Conversion<String>() {
        @Override
        public String toSSF(long timestamp, String device, String s) {
            // Redelegate
            return unescapedString.toSSF(SSF_TAG, timestamp, device, s);
        }
    };

    /**
     * Converts an activity recognition result into the SSF format
     */
    public static final Conversion<ActivityRecognitionResult> activityRecognitionResult = new Conversion<ActivityRecognitionResult>() {
        /**
         * Utility method for converting the activity type into a readable string
         */
        private String getActivityNameFromType(int activityType) {
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

        @Override
        public String toSSF(long timestamp, String device, ActivityRecognitionResult activityRecognitionResult) {
            // Get most probable
            final DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            // Insert to SSF
            return objects.toSSF(SSF_GOOGLE_ACTIVITY, timestamp, device, new Object[]{getActivityNameFromType(detectedActivity.getType()), detectedActivity.getConfidence()});
        }
    };
}
