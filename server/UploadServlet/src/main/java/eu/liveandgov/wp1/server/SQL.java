package eu.liveandgov.wp1.server;

/**
 * Created by hartmann on 9/30/14.
 */
public class SQL {
    public static final String CREATE_TRIP_TABLE = "CREATE TABLE IF NOT EXISTS trip (trip_id SERIAL, user_id VARCHAR(36), start_ts BIGINT, stop_ts BIGINT, name VARCHAR(255));";
    public static final String CREATE_AUTH_TABLE = "CREATE TABLE IF NOT EXISTS auth (user_id VARCHAR(36) PRIMARY KEY, secret VARCHAR(255));";
}
