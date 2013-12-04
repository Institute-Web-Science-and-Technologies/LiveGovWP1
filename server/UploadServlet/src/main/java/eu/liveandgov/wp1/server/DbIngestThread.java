package eu.liveandgov.wp1.server;

import eu.liveandgov.wp1.server.db_helper.BatchInserter;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import org.apache.log4j.Logger;
import org.jeromq.ZMQ;

import java.io.*;
import java.sql.SQLException;

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
        System.out.println("DbIngestThread Litening on " + ZMQ_ADDRESS);
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

                File outFile = new File(message);

                BufferedReader reader;
                reader = new BufferedReader(new FileReader(outFile));

                int rows = BatchInserter.batchInsertFile(db, reader);

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
