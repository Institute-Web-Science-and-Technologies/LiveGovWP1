package eu.liveandgov.wp1.sensor_miner;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import eu.liveandgov.wp1.sensor_miner.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;

/**
 * Convenience class that makes various context attributes accessible from a static context.
 *
 * Created by hartmann on 9/29/13.
 */
public class GlobalContext {
    public static ServiceSensorControl context;

    public static void set(ServiceSensorControl newContext) {
        context = newContext;
    }

    public static SensorManager getSensorManager(){
        if (context == null) throw new IllegalStateException("Context not initialized");
        return (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

    }

    public static WifiManager getWifiManager(){
        if (context == null) throw new IllegalStateException("Context not initialized");
        return (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    }

    public static String getUserId() {
        if (context == null) throw new IllegalStateException("Context not initialized");
        return context.userId;
    }

    public static SensorQueue getSensorQueue() {
        if (context == null) throw new IllegalStateException("Context not initialized");
        return context.sensorQueue;
    }

    public static void sendLog(String message){
        if (context == null) throw new IllegalStateException("Context not initialized");
        Intent intent = new Intent(ExtendedIntentAPI.RETURN_LOG);
        intent.putExtra(ExtendedIntentAPI.FIELD_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
