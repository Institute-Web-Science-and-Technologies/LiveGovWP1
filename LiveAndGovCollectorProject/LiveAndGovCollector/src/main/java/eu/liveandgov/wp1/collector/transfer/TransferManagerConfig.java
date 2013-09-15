package eu.liveandgov.wp1.collector.transfer;

/**
 *
 * Global Transfer Options
 *
 * Created by hartmann on 9/15/13.
 */
public class TransferManagerConfig {

    public static final int UPDATE_INTERVAL = 1000; // 1 sec in ms
    public static final int INITIAL_WAIT = 1000; // 10000; // 10 sec in ms
    //    public static final String UPLOAD_URL = "http://141.26.71.84:/backend/upload";
    public static final String REMOTE_HOST = "141.26.71.84";
    public static final String REMOTE_PORT = "8080";
    public static final String UPLOAD_URL = String.format("http://%s:%s/", REMOTE_HOST, REMOTE_PORT);
    public static final String FIELD_NAME = "upfile";

    // Allowed Transfer option
    public static final boolean ENABLE_WIFI = true;
    public static final boolean ENABLE_MOBILE = false;
    public static final int MIN_TRANSFER_RECORDS = 50;
    public static final int MAX_TRANSFER_RECORDS = 1000;

}
