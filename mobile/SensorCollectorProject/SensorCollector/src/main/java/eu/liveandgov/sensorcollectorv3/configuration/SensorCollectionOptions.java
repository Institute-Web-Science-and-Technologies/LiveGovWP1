package eu.liveandgov.sensorcollectorv3.configuration;

/**
 * Created by hartmann on 9/26/13.
 */
public class SensorCollectionOptions {
    /**
     * Upload URL and Sockets
     */
    public static String REMOTE_HOST = "141.26.71.84"; // LG Server
    public static String UPLOAD_ZMQ_SOCKET = "tcp://" + REMOTE_HOST + ":5555";
    public static String UPLOAD_URL = "http://141.26.71.84:8080/UploadServlet/";
//     public static String UPLOAD_URL = "http://" + REMOTE_HOST + ":6000/";

    /**
     * Accelerometer Sensor
     */
    public static final boolean REC_ACC = true;

    /**
     * Linear Acceleration
     * i.e. Acceleration - Gravity force
     */
    public static final boolean REC_LIN_ACC = true;

    /**
     * Gravity
     */
    public static final boolean REC_GRAV = true;

    /**
     * GPS
     */
    public static final boolean REC_GPS = true;

    /**
     * Networking Samples
     */
    public static final boolean WIFI = true;   // wifi
    public static final boolean GSM = true;    // GSM
    public static final boolean BLT = true;    // Bluetooth

    /**
     * Record Activity
     * using the new Google Activity API
     */
    public static final boolean REC_G_ACT = true;
}