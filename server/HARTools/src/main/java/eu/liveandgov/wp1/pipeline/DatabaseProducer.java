package eu.liveandgov.wp1.pipeline;

import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.impl.Acceleration;

import java.sql.*;

/**
 * Created by cehlen on 25/02/14.
 */
public class DatabaseProducer extends Producer<Tuple<Long, Acceleration>> {

    private final int VALUES_PER_REQUEST = 1024;

    private String username;
    private String password;
    private String database;

    private Connection connection = null;

    public DatabaseProducer(String username, String password, String database) {
        this.username = username;
        this.password = password;
        this.database = database;

        connect();
    }

    public void start() {
        int current_offset = 0;
        int numSamples = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS c FROM sensor_accelerometer");
            if (rs.next()) {
                numSamples = rs.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM sensor_accelerometer LIMIT " + VALUES_PER_REQUEST + " OFFSET " + current_offset);
                boolean empty = true;
                while (rs.next()) {
                    long ts = rs.getLong("ts");
                    Long trip_id = (long)rs.getInt("trip_id");
                    float x = rs.getFloat("x");
                    float y = rs.getFloat("y");
                    float z = rs.getFloat("z");
                    float values[] = new float[] {x, y, z};
                    Acceleration acc = new Acceleration(ts, "None", values);
                    Tuple<Long, Acceleration> value = new Tuple<Long, Acceleration>(trip_id, acc);
                    produce(value);
                    empty = false;
                }
                if (empty) {
                    break;
                }
                System.out.println(current_offset + " rows done. (" + (Math.round(current_offset / (float) numSamples * 10000.0) / 100.0) + "%)");
                current_offset += VALUES_PER_REQUEST;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
