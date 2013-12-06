package eu.liveandgov.wp1.sensor_miner.sensors;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.telephony.NeighboringCellInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;
import java.util.Locale;

import eu.liveandgov.wp1.human_activity_recognition.containers.MotionSensorValue;
import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers.BluetoothHolder;
import eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers.TelephonyHolder;
import eu.liveandgov.wp1.sensor_miner.sensors.sensor_value_objects.GpsSensorValue;

import static eu.liveandgov.wp1.sensor_miner.configuration.SsfFileFormat.*;

/**
 * Converts sensor events into the ssf Format.
 *
 * Created by hartmann on 9/15/13.
 */
public class SensorSerializer {

    private static long timestampCorrectionMs = 0;

    static {
        // If build-version is above jelly-bean mr1 (17), timestamps of the sensors are already in
        // utc, otherwise convert by rebasing them on the uptime
        //
        // We comute a global correction based on the fact, that currentTimeMillis is in UTC
        // and nanoTime is in uptime.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            timestampCorrectionMs = (long) (System.currentTimeMillis() - (System.nanoTime() / 1E6) );
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


    public static String fromSensorEvent(SensorEvent event) {
        int sensorType= event.sensor.getType();

        long timestamp_ms = (long) (event.timestamp / 1E6) + timestampCorrectionMs;

        if (sensorType == Sensor.TYPE_ACCELEROMETER){
            return fillStringFloats(SSF_ACCELEROMETER, timestamp_ms, GlobalContext.getUserId(), event.values);
        } else if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION){
            return fillStringFloats(SSF_LINEAR_ACCELERATION, timestamp_ms, GlobalContext.getUserId(), event.values);
        } else if (sensorType == Sensor.TYPE_GRAVITY) {
            return fillStringFloats(SSF_GRAVITY, timestamp_ms, GlobalContext.getUserId(), event.values);
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            return fillStringFloats(SSF_GYROSCOPE, timestamp_ms, GlobalContext.getUserId(), event.values);
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            return fillStringFloats(SSF_MAGNETOMETER, timestamp_ms, GlobalContext.getUserId(), event.values);
        } else if (sensorType == Sensor.TYPE_ROTATION_VECTOR) {
            return fillStringFloats(SSF_ROTATION, timestamp_ms, GlobalContext.getUserId(), event.values);
        }
        return "ERR," + timestamp_ms + ",,Unknown sensor " + sensorType;
    }

    public static String fromScanResults(List<ScanResult> scanResults) {
        final StringBuilder builder = new StringBuilder();
        boolean separate = false;
        for(ScanResult scanResult : scanResults)
        {
            if(separate)
            {
                // Separate entries of the scan result list by semicolon
                builder.append(';');
            }

            // Write each scan result as a tuple of Escaped SSID/Escaped BSSID/Frequency in MHz/Level in dBm
            builder.append( "\"" + StringEscapeUtils.escapeJava(scanResult.SSID) + "\"");
            builder.append('/');
            builder.append( "\"" + StringEscapeUtils.escapeJava(scanResult.BSSID) + "\"");
            builder.append('/');
            builder.append(scanResult.frequency);
            builder.append('/');
            builder.append(scanResult.level);

            separate = true;
        }

         return fillString(SSF_WIFI, System.currentTimeMillis(), GlobalContext.getUserId(), builder.toString());
    }

    public static String intermediateFromBTFound(Intent intent)
    {
        final StringBuilder builder = new StringBuilder();

        // Extract the stored data from the bundle of the intent
        final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        final BluetoothClass bluetoothClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
        final String name = intent.hasExtra(BluetoothDevice.EXTRA_NAME) ? intent.getStringExtra(BluetoothDevice.EXTRA_NAME) : null;
        final Short rssi = intent.hasExtra(BluetoothDevice.EXTRA_RSSI) ? intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) : null;

        // Write the values as a tuple of Escaped Address/Device Major Class/Device Class/Bond State/Optional Escaped Name/Optional RSSI
        builder.append("\"" + StringEscapeUtils.escapeJava(bluetoothDevice.getAddress()) + "\"");
        builder.append('/');
        builder.append(BluetoothHolder.getDeviceMajorClassName(bluetoothClass.getMajorDeviceClass()));
        builder.append('/');
        builder.append(BluetoothHolder.getDeviceClassName(bluetoothClass.getDeviceClass()));
        builder.append('/');
        builder.append(BluetoothHolder.getBondName(bluetoothDevice.getBondState()));
        builder.append('/');
        if(name != null)
        {
            builder.append("\"" + StringEscapeUtils.escapeJava(name) + "\"");
        }
        builder.append('/');
        if(rssi != null)
        {
            builder.append(rssi);
        }

        // Return the created value
        return builder.toString();
    }

    public static String fromBluetooth(String accumulatedIntermediate)
    {
        return fillString(SSF_BLUETOOTH, System.currentTimeMillis(), GlobalContext.getUserId(), accumulatedIntermediate);
    }

    public static String fromPhoneState(ServiceState serviceState, SignalStrength signalStrength, List<NeighboringCellInfo> neighboringCellInfos)
    {
        final StringBuilder builder = new StringBuilder();

        // Write phone status as Service State/Roaming State/Manual Selection State/Operator Name/Phone Signal Strength
        builder.append(TelephonyHolder.getStateName(serviceState.getState()));
        builder.append('/');
        builder.append(TelephonyHolder.getRoamingText(serviceState.getRoaming()));
        builder.append('/');
        builder.append(TelephonyHolder.getManualModeText(serviceState.getIsManualSelection()));
        builder.append('/');
        builder.append("\"" + StringEscapeUtils.escapeJava(serviceState.getOperatorAlphaLong()) + "\"");
        builder.append('/');
        builder.append("\"" + StringEscapeUtils.escapeJava(TelephonyHolder.getSignalStrengthText(signalStrength)) + "\"");

        // Separate prefix from cells with colon
        builder.append(':');

        // Write each cell info as a tuple of Escaped Identity/Network Type/Signal Strength in dBm
        boolean separate = false;
        for(NeighboringCellInfo neighboringCellInfo : neighboringCellInfos)
        {
            if(separate)
            {
                // Separate entries of the scan result list by semicolon
                builder.append(';');
            }

            builder.append("\"" + StringEscapeUtils.escapeJava(TelephonyHolder.getIdentityText(neighboringCellInfo.getCid(), neighboringCellInfo.getLac())) + "\"");
            builder.append('/');
            builder.append(StringEscapeUtils.escapeJava(TelephonyHolder.getNetworkTypeText(neighboringCellInfo.getNetworkType())));
            builder.append('/');
            builder.append(TelephonyHolder.getTS27SignalStrengthText(neighboringCellInfo.getRssi()));

            separate = true;
        }

        return fillString(SSF_GSM, System.currentTimeMillis(), GlobalContext.getUserId(), builder.toString());
    }

    public static String fromLocation(Location location) {
        return fillStringDoubles(SSF_GPS, System.currentTimeMillis(), GlobalContext.getUserId(), new double[]{location.getLatitude(), location.getLongitude(), location.getAltitude()});
    }

    public static String fromTag(String tag) {
        return fillDefaults(SSF_TAG, "\""+ StringEscapeUtils.escapeJava(tag) +"\"");
    }

    // GOOGLE ACTIVITY RECOGNITION HELPER

    public static String fromGoogleActivity(ActivityRecognitionResult result) {
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        return fillStringObjects(SSF_GOOGLE_ACTIVITY, result.getTime(), GlobalContext.getUserId(),
                new Object[]{getActivityNameFromType(detectedActivity.getType()),
                        detectedActivity.getConfidence()});
    }

    private static String getActivityNameFromType(int activityType) {
        switch(activityType) {
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
        }
        return "unknown";
    }

    /**
     * Writes sensor values in SSF format. (cf. project Wiki)
     * @param type of Sensor
     * @param timestamp of recording in ms
     * @param deviceId unique device identified
     * @param value String
     * @return ssfRow
     */
    private static String fillString(String type, long timestamp, String deviceId, String value) {
        return String.format(Locale.ENGLISH, "%s,%d,%s,%s", type, timestamp, deviceId, value);
    }

    private static String fillStringFloats(String type, long timestamp, String deviceId, float[] values) {
        String valueString = "";
        for (float value : values) {
            valueString += value + " ";
        }
        return fillString(type, timestamp, deviceId, valueString);
    }

    private static String fillStringDoubles(String type, long timestamp, String deviceId, double[] values) {
        String valueString = "";
        for (double value : values) {
            valueString += value + " ";
        }
        return fillString(type, timestamp, deviceId, valueString);
    }

    private static String fillStringObjects(String type, long timestamp, String deviceId, Object[] values) {
        String valueString = "";
        for (Object value : values) {
            valueString += value + " ";
        }
        return fillString(type, timestamp, deviceId, valueString);
    }

    public static String fillDefaults(String prefix, String value) {
        return fillString(prefix, System.currentTimeMillis(), GlobalContext.getUserId(), value);
    }
}
