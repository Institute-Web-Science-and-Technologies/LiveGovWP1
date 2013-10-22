package eu.liveandgov.wp1.server;

import eu.liveandgov.wp1.server.db_helper.BatchInserter;
import eu.liveandgov.wp1.server.sensor_helper.*;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.*;
import org.apache.log4j.Logger;
import org.jeromq.ZMQ;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * User: hartmann
 * Date: 10/19/13
 */
public class DbIngestThread implements Runnable {
    // public static final String IMPORT_DIR = "/srv/liveandgov/UploadServletRawFiles/";
    public static final String ZMQ_ADDRESS = "tcp://127.0.0.1:50111";
    private static final Logger LOG = Logger.getLogger(DbIngestThread.class);

    ZMQ.Socket socket;

    public static void main(String[] args) {
        DbIngestThread instance = new DbIngestThread();
        instance.run();
    }

    public void run(){
        System.out.println("Hello from DBThread");
        LOG.info("Starting Ingest Thread");
        socket = ZMQ.context().socket(ZMQ.SUB);
        socket.subscribe(""); // all topics
        socket.bind(ZMQ_ADDRESS);

        PostgresqlDatabase db = new PostgresqlDatabase();

        String message;
        File sensorFile = null;
        while (true) {
            try {
                message = socket.recvStr();
                LOG.info("Received message:" + message);
                System.out.println("rec:" + message);

                if (message.equals("STOP")) { break; }

                sensorFile = new File(message);
                int rows = BatchInserter.batchInsertFile(db, sensorFile);

                LOG.info("Imported files int db. Rows: " + rows);

            } catch (FileNotFoundException e) {
                LOG.info("File not found: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                LOG.info("Error reading file: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                LOG.info("Error writing to database.");
                e.printStackTrace();
            } catch (Exception e) {
                LOG.info("Something (else) went wrong.");
                e.printStackTrace();
            }
        }
        socket.close();
    }

}
