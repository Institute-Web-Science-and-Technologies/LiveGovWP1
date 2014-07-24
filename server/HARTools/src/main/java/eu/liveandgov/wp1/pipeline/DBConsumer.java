package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.data.Tuple;

import java.sql.*;

/**
 * Created by cehlen on 04/03/14.
 */
public class DBConsumer implements Consumer<Triple<Long, Long, String>> {
    private String username;
    private String password;
    private String database;

    private Connection connection = null;
    private PreparedStatement batchStatement = null;

    private int lastTripId = -1;
    private long lastTs = -1;
    private String lastActivity = "";
    private final long MIN_DIFFERENCE = 2000;

    private final String INSERT_QUERY = "INSERT INTO har_annotation2 (trip_id, ts, tag) values (?,?,?)";

    private final int INSERTS_PER_BATCH = 1024;
    private int currentNumInserts = 0;

    public DBConsumer(String username, String password, String database) {
        this.username = username;
        this.password = password;
        this.database = database;

        connect();
    }

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("No PostgreSQL JDBC Driver found.");
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/" + database + "?autoReconnect=true",
                    username, password);
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS har_annotation2 (trip_id integer, ts bigint, tag text)");
            batchStatement = connection.prepareStatement(INSERT_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() {
        if (connection != null) {
            try {
                batchStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void push(Triple<Long, Long, String> longLongStringTriple) {
        int trip_id = longLongStringTriple.left.intValue();
        if (trip_id != lastTripId) {
            lastTripId = trip_id;
            lastTs = longLongStringTriple.center;
            lastActivity = longLongStringTriple.right;
        } else if (lastActivity.equals(longLongStringTriple.right) && longLongStringTriple.center < lastTs + MIN_DIFFERENCE) {
            return;
        }
        try {
            batchStatement.setInt(1, trip_id);
            batchStatement.setLong(2, longLongStringTriple.center);
            batchStatement.setString(3, longLongStringTriple.right);
            batchStatement.addBatch();
            currentNumInserts += 1;
            lastTs = longLongStringTriple.center;
            lastActivity = longLongStringTriple.right;
            lastTripId = trip_id;
            if (currentNumInserts >= INSERTS_PER_BATCH) {
                executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeQuery() {
        System.out.println("Insert into db...");
        try {
            batchStatement.executeBatch();
            batchStatement.close();
            currentNumInserts = 0;
            batchStatement = connection.prepareStatement(INSERT_QUERY);
        } catch (SQLException e) {
            e.getNextException().printStackTrace();
            e.printStackTrace();
        }

    }
}
