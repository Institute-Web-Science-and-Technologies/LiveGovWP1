package eu.liveandgov.wp1.sensor_miner.configuration;

import android.hardware.SensorManager;

/**
 * Created by hartmann on 9/26/13.
 */
public class SensorCollectionOptions {
    // CONNECTIVITY //
    public static String REMOTE_HOST = "141.26.71.84"; // LG Server
    public static String UPLOAD_URL = "http://" + REMOTE_HOST + ":8080/UploadServlet/";
    public static String STREAMING_ZMQ_SOCKET = "tcp://" + REMOTE_HOST + ":5555";

    public static final boolean ZIPPED_PERSISTOR = false;
    public static final boolean API_EXTENSIONS = true;

    // SENSOR RECORDING OPTIONS //

    // GPS
    public static final boolean REC_GPS = true;
    public static final int GPS_DELAY_MS = 5000; // delay of gps rescan in milli seconds

    // Motion sensors
    public static final int REC_ACC = SensorOptions.ON_FAST;
    public static final int REC_LINEAR_ACC = SensorOptions.ON_FAST;
    public static final int REC_GRAVITY_ACC = SensorOptions.ON_FAST;
    public static final int REC_GYROSCOPE = SensorOptions.ON_SLOW;
    public static final int REC_MAGNETOMETER = SensorOptions.ON_SLOW;
    public static final int REC_ROTATION = SensorOptions.ON_SLOW;

    // Network samples
    public static final boolean REC_WIFI = true;   // wifi
    public static final int WIFI_SCAN_DELAY_MS = 20000; // delay in milli seconds
    public static final boolean REC_BLT = false;    // Bluetooth
    public static final int BLT_SCAN_DELAY_MS = 20000; // delay in milli seconds
    public static final boolean REC_GSM = false;    // GSM

    /**
     * Record Activity using the new Google Activity API
     */
    public static final boolean REC_G_ACT = true;

    public static class SensorOptions {
        public static int OFF = -1;
        public static int ON_FASTEST = SensorManager.SENSOR_DELAY_FASTEST;
        public static int ON_FAST = SensorManager.SENSOR_DELAY_GAME;
        public static int ON_MEDIUM = SensorManager.SENSOR_DELAY_UI;
        public static int ON_SLOW = SensorManager.SENSOR_DELAY_NORMAL;
    }
}
