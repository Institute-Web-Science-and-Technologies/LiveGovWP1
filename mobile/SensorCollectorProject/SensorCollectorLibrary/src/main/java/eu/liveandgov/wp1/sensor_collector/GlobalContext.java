package eu.liveandgov.wp1.sensor_collector;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

import static junit.framework.Assert.assertNotNull;

/**
 * Convenience class that makes various context attributes accessible from a static context.
 * <p/>
 * Created by hartmann on 9/29/13.
 */
public class GlobalContext {
    /**
     * Acquire a logger for domain service and for item global context
     */
    private static final Logger log = LogPrincipal.get();

    public static ServiceSensorControl context;

    public static void set(ServiceSensorControl newContext) {
        context = newContext;
    }

    public static ScheduledExecutorService getExecutorService() {
        assertNotNull(context);
        return context.executorService;
    }

    public static LocationManager getLocationManager() {
        assertNotNull(context);
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static SensorManager getSensorManager() {
        assertNotNull(context);
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static WifiManager getWifiManager() {
        assertNotNull(context);
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static TelephonyManager getTelephonyManager() {
        assertNotNull(context);
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static String getUserId() {
        assertNotNull(context);
        return context.userId;
    }

    public static SensorQueue getSensorQueue() {
        assertNotNull(context);
        return context.sensorQueue;
    }

    public static void sendLog(String message) {
        assertNotNull(context);
        Intent intent = new Intent(ExtendedIntentAPI.RETURN_LOG);
        intent.putExtra(ExtendedIntentAPI.FIELD_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    public static File getFileRoot() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            log.debug("Extenal storage available");
            return Environment.getExternalStorageDirectory();
        }

        assertNotNull(context);

        log.debug("Extenal storage not available");
        return context.getFilesDir();
    }

}
