package eu.liveandgov.wp1.server;

/**
 * Created by hartmann on 9/9/14.
 */
public class CONFIG {
    static final String OUT_DIR = "/srv/liveandgov/UploadServletRawFiles/";

    public static final String DB_NAME = "liveandgov_dev";
    public static final String DB_USER = "liveandgov";
    public static final String DB_PASS = "liveandgov";

    public static final String JDBC_CONNECTION = "jdbc:postgresql://127.0.0.1:5432/"+ CONFIG.DB_NAME +"?autoReconnect=true";
}
