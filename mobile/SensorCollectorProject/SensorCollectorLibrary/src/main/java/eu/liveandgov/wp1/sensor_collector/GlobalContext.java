package eu.liveandgov.wp1.sensor_collector;

import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;

import static junit.framework.Assert.assertNotNull;

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

    public static LocationManager getLocationManager(){
        assertNotNull(context);
        return (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    public static SensorManager getSensorManager(){
        assertNotNull(context);
        return (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
    }

    public static WifiManager getWifiManager(){
        assertNotNull(context);
        return (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    }

    public static TelephonyManager getTelephonyManager(){
        assertNotNull(context);
        return (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
    }

    public static String getUserId() {
        assertNotNull(context);
        return context.userId;
    }

    public static SensorQueue getSensorQueue() {
        assertNotNull(context);
        return context.sensorQueue;
    }

    public static void sendLog(String message){
        assertNotNull(context);
        Intent intent = new Intent(ExtendedIntentAPI.RETURN_LOG);
        intent.putExtra(ExtendedIntentAPI.FIELD_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
