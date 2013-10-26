package eu.liveandgov.sensorcollectorv3;

import android.content.Intent;
import android.hardware.SensorManager;
import android.provider.Settings;

import eu.liveandgov.sensorcollectorv3.configuration.ExtendedIntentAPI;
import eu.liveandgov.sensorcollectorv3.configuration.IntentAPI;

/**
 * Convenience class that makes various context attributes accessible from a static context.
 *
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
        Intent intent = new Intent(ExtendedIntentAPI.RETURN_LOG);
        intent.putExtra(ExtendedIntentAPI.FIELD_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
