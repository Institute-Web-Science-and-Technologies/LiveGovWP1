package eu.liveandgov.sensorcollectorv3;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.provider.Settings;

import eu.liveandgov.sensorcollectorv3.Configuration.IntentAPI;

/**
 * Created by hartmann on 9/29/13.
 */
public class GlobalContext {
    public static ServiceSensorControl context;
    public static SensorManager sensorManager;
    public static String androidId;

    public static void set(ServiceSensorControl newContext) {
        context = newContext;
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        androidId = Settings.Secure.getString(GlobalContext.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void sendLog(String message){
        Intent intent = new Intent(IntentAPI.RETURN_LOG);
        intent.putExtra(IntentAPI.FIELD_LOG, message);
        context.sendBroadcast(intent);
    }
}
