package eu.liveandgov.sensorcollectorv3.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.provider.Settings;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import eu.liveandgov.sensorcollectorv3.GlobalContext;

/**
 * Created by hartmann on 9/15/13.
 */
public class SensorParser {

    private static String id = Settings.Secure.getString(GlobalContext.context.getContentResolver(),
            Settings.Secure.ANDROID_ID);

    public static void setId(String id){
        SensorParser.id = id;
    }

    public static String parse(SensorEvent event) {
        int sensorType= event.sensor.getType();
        if ( sensorType == Sensor.TYPE_ACCELEROMETER){
            return fillString("ACC", event.timestamp / 1000, id, event.values);
        } else if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION){
            return fillString("LAC", event.timestamp / 1000, id, event.values);
        } else if (sensorType == Sensor.TYPE_GRAVITY) {
            return fillString("GRA", event.timestamp / 1000, id, event.values);
        }
        return "ERR,,,Unknown sensor " + sensorType;
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
