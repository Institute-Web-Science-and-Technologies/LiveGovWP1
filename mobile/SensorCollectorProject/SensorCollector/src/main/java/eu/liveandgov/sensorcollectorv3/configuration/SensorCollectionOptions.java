package eu.liveandgov.sensorcollectorv3.configuration;

/**
 * Created by hartmann on 9/26/13.
 */
public class SensorCollectionOptions {
    /**
     * Upload URL and Sockets
     */
    public static String REMOTE_HOST = "141.26.71.84"; // LG Server
    public static String UPLOAD_URL = "http://" + REMOTE_HOST + ":8080/UploadServlet/";
    public static String STREAMING_ZMQ_SOCKET = "tcp://" + REMOTE_HOST + ":5555";

    public static final boolean ZIPPED_PERSISTOR = false;
    public static final boolean API_EXTENSIONS = false;

    //* SENSOR RECORDING *//

    /**
     * GPS
     */
    public static final boolean REC_GPS = true;

    /**
     * Accelerometer Sensor
     */
    public static final boolean REC_ACC = true;
    public static final boolean REC_LINEAR_ACC = true;
    public static final boolean REC_GRAVITY_ACC = true;

    /**
     * Motion Sensors
     */
    public static final boolean REC_GYROSCOPE = true;
    public static final boolean REC_MAGNETOMETER = true;

    /**
     * Networking Samples
     */
    public static final boolean REC_WIFI = true;   // wifi
    public static final boolean REC_GSM = true;    // GSM
    public static final boolean REC_BLT = true;    // Bluetooth

    /**
     * Record Activity
     * using the new Google Activity API
     */
    public static final boolean REC_G_ACT = true;
}
