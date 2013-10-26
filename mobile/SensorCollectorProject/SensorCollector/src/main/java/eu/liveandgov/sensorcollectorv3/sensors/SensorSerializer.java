package eu.liveandgov.sensorcollectorv3.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.provider.Settings;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import eu.liveandgov.sensorcollectorv3.GlobalContext;

/**
 * Converts sensor events into the ssf Format.
 *
 * Created by hartmann on 9/15/13.
 */
public class SensorSerializer {

    private static String id = Settings.Secure.getString(GlobalContext.context.getContentResolver(),
            Settings.Secure.ANDROID_ID);

    public static void setId(String id){
        SensorSerializer.id = id;
    }

    public static MotionSensorValue parseEvent(String event) {
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

    public static String parse(SensorEvent event) {
        int sensorType= event.sensor.getType();
        // event.timestamp is in ns = 1E-9 sec.
        long timestamp_ms = (long) (event.timestamp / 1E6);
        if ( sensorType == Sensor.TYPE_ACCELEROMETER){
            return fillString("ACC", timestamp_ms , id, event.values);
        } else if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION){
            return fillString("LAC", timestamp_ms, id, event.values);
        } else if (sensorType == Sensor.TYPE_GRAVITY) {
            return fillString("GRA", timestamp_ms, id, event.values);
        }
        return "ERR," + timestamp_ms + ",,Unknown sensor " + sensorType;
    }

    public static String parse(Location location) {
        return fillString("GPS", location.getTime(), id, new double[]{location.getLatitude(), location.getLongitude(), location.getAltitude()});
    }

    public static String parse(ActivityRecognitionResult result) {
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        return fillString("ACT", result.getTime(), id,
                new Object[]{getActivityNameFromType(detectedActivity.getType()),
                        detectedActivity.getConfidence()});
    }

    public static String parse(String tag) {
        return String.format("%s,%d,%s,\"%s\"", "TAG", System.currentTimeMillis(), id, tag);
    }

    /**
     * Writes sensor values in SSF format. (cf. project Wiki)
     * @param type of Sensor
     * @param timestamp of recording in ms
     * @param deviceId unique device identified
     * @param values float array
     * @return ssfRow
     */
    private static String fillString(String type, long timestamp, String deviceId, float[] values) {
        String msg = String.format("%s,%d,%s,", type, timestamp, deviceId);
        for (float value : values) {
            msg += value + " ";
        }
        return msg;
    }

    private static String fillString(String type, long timestamp, String deviceId, double[] values) {
        String msg = String.format("%s,%d,%s,", type, timestamp, deviceId);
        for (double value : values) {
            msg += value + " ";
        }
        return msg;
    }

    private static String fillString(String type, long timestamp, String deviceId, Object[] values) {
        String msg = String.format("%s,%d,%s,", type, timestamp, deviceId);
        for (Object value : values) {
            msg += value + " ";
        }
        return msg;
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

}
