package eu.liveandgov.wp1.sensor_collector.configuration;

import android.hardware.SensorManager;
import android.os.Build;

/**
 * Created by hartmann on 9/26/13.
 */
public class SensorCollectionOptions {
    // SERVICE
    public static final int MAIN_EXECUTOR_CORE_POOL = 3;

    public static final long MAIN_EXECUTOR_CORE_TIMEOUT = 5000L;

    // MONITORING

    public static final long MONITORING_RATE = 2000L;

    // CONNECTIVITY //
    public static String REMOTE_HOST = "141.26.248.40"; // LG Server
    public static String UPLOAD_URL = "http://" + REMOTE_HOST + ":8080/UploadServlet/";
    public static String STREAMING_ZMQ_SOCKET = "tcp://" + REMOTE_HOST + ":5555";

    public static final boolean ZIPPED_PERSISTOR = true;
    public static final boolean API_EXTENSIONS = true;

    // SENSOR RECORDING OPTIONS //

    // GPS
    public static final boolean REC_GPS = true;
    public static final int GPS_DELAY_MS = 5000; // delay of gps rescan in milli seconds

    // Motion sensors
    public static final int REC_ACC = SensorOptions.ON_GAME;
    public static final int REC_LINEAR_ACC = SensorOptions.OFF;
    public static final int REC_GRAVITY_ACC = SensorOptions.OFF;
    public static final int REC_GYROSCOPE = SensorOptions.OFF;
    public static final int REC_MAGNETOMETER = SensorOptions.OFF;
    public static final int REC_ROTATION = SensorOptions.OFF;

    // Network samples
    public static final boolean REC_WIFI = true;   // wifi
    public static final int WIFI_SCAN_DELAY_MS = 20000; // delay in milli seconds
    public static final boolean REC_BLT = true;    // Bluetooth
    public static final int BLT_SCAN_DELAY_MS = 20000; // delay in milli seconds
    public static final boolean REC_GSM = true;    // GSM
    public static final int GSM_SCAN_DELAY_MS = 20000; // delay in milli seconds

    // Ask for user setup if these flags are activated
    public static final boolean ASK_GPS = true;
    public static final boolean ASK_BLT = false;
    public static final boolean ASK_WIFI = false;

    /**
     * Record Activity using the new Google Activity API
     */
    public static final boolean REC_G_ACT = false;

    public static class SensorOptions {
        public static int OFF = -1;
        public static int ON_FASTEST = SensorManager.SENSOR_DELAY_FASTEST; // 100Hz
        public static int ON_GAME =SensorManager.SENSOR_DELAY_GAME; // 40Hz
        public static int ON_UI = SensorManager.SENSOR_DELAY_UI; // 20Hz
        public static int ON_NORMAL = SensorManager.SENSOR_DELAY_NORMAL; // 10Hz
    }
}
