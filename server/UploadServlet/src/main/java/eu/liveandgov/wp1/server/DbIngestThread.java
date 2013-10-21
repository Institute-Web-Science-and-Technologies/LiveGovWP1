package eu.liveandgov.wp1.server;

import eu.liveandgov.wp1.server.SensorValueObjects.*;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
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

        String message;
        File sensorFile = null;
        while (true) {
            try {
                message = socket.recvStr();
                LOG.info("Received message:" + message);
                System.out.println("rec:" + message);

                if (message.equals("STOP")) { break; }

                sensorFile = new File(message);
                InputStream IS = new FileInputStream(sensorFile);
                int rows = saveToDatabase(IS);
                LOG.info("Imported files int db. Rows: " + rows);

            } catch (FileNotFoundException e) {
                LOG.info("File not found: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                LOG.info("Error writing to database.");
                e.printStackTrace();
            } catch (IOException e) {
                LOG.info("Error writing to database.");
                e.printStackTrace();
            } catch (Exception e) {
                LOG.info("Something (else) went wrong.");
                e.printStackTrace();
            }
        }
        socket.close();
    }

    private int saveToDatabase(InputStream input) throws IOException, SQLException {
        PostgresqlDatabase db = new PostgresqlDatabase("liveandgov", "liveandgov");

        PreparedStatement psAcc, psGPS, psTag, psAct, psLac, psGra;
        Timestamp ts;

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        psAcc = db.connection
                .prepareStatement("INSERT INTO accelerometer VALUES (?, ?, ?, ?, ?)");
        psGPS = db.connection
                .prepareStatement("INSERT INTO gps VALUES (?, ?, ST_GeomFromText(?,4326))");
        psTag = db.connection
                .prepareStatement("INSERT INTO tags VALUES (?, ?, ?)");
        psAct = db.connection
                .prepareStatement("INSERT INTO google_activity VALUES (?, ?, ?)");
        psLac = db.connection
                .prepareStatement("INSERT INTO linear_acceleration VALUES (?, ?, ?, ?, ?)");
        psGra = db.connection
                .prepareStatement("INSERT INTO gravity VALUES (?, ?, ?, ?, ?)");

        int count = 0;
        while (reader.ready()) {
            RawSensorValue rsv = RawSensorValue.fromString(reader.readLine());
            switch (rsv.type) {
                case ACC:
                    AccSensorValue asv = AccSensorValue.fromRSV(rsv);
                    psAcc.setString(1, asv.id);
                    ts = new Timestamp(asv.timestamp);
                    psAcc.setTimestamp(2, ts);
                    psAcc.setFloat(3, asv.x);
                    psAcc.setFloat(4, asv.y);
                    psAcc.setFloat(5, asv.z);
                    psAcc.addBatch();
                    break;
                case GPS:
                    GPSSensorValue gsv = GPSSensorValue.fromRSV(rsv);
                    psGPS.setString(1, gsv.id);
                    ts = new Timestamp(gsv.timestamp);
                    psGPS.setTimestamp(2, ts);
                    psGPS.setString(3, "POINT(" + Double.toString(gsv.longitude)
                            + ' ' + Double.toString(gsv.latitude) + ")");
                    psGPS.addBatch();
                    break;
                case TAG:
                    TagSensorValue tsv = TagSensorValue.fromRSV(rsv);
                    psTag.setString(1, tsv.id);
                    ts = new Timestamp(tsv.timestamp);
                    psTag.setTimestamp(2, ts);
                    psTag.setString(3, tsv.tag);
                    psTag.addBatch();
                    break;
                case ACT:
                    GoogleActivitySensorValue gasv = GoogleActivitySensorValue.fromRSV(rsv);
                    psAct.setString(1, gasv.id);
                    ts = new Timestamp(gasv.timestamp);
                    psAct.setTimestamp(2, ts);
                    psAct.setString(3, gasv.activity);
                    psAct.addBatch();
                    break;
                case LAC:
                    LacSensorValue lasv = LacSensorValue.fromRSV(rsv);
                    psLac.setString(1, lasv.id);
                    ts = new Timestamp(lasv.timestamp);
                    psLac.setTimestamp(2, ts);
                    psLac.setFloat(3, lasv.x);
                    psLac.setFloat(4, lasv.y);
                    psLac.setFloat(5, lasv.z);
                    psLac.addBatch();
                    break;
                case GRA:
                    GraSensorValue grasv = GraSensorValue.fromRSV(rsv);
                    psGra.setString(1, grasv.id);
                    ts = new Timestamp(grasv.timestamp);
                    psGra.setTimestamp(2, ts);
                    psGra.setFloat(3, grasv.x);
                    psGra.setFloat(4, grasv.y);
                    psGra.setFloat(5, grasv.z);
                    psGra.addBatch();
                    break;
                default:
                    count--;
                    break;
            }
            count++;
        }
        psAcc.executeBatch();
        psGPS.executeBatch();
        psTag.executeBatch();
        psAct.executeBatch();
        psLac.executeBatch();
        psGra.executeBatch();
        psAcc.close();
        psGPS.close();
        psTag.close();
        psAct.close();
        psLac.close();
        psGra.close();
        return count;
    }

}
