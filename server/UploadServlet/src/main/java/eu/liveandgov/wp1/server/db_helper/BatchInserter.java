package eu.liveandgov.wp1.server.db_helper;

import eu.liveandgov.wp1.server.sensor_helper.SampleType;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueFactory;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * User: hartmann
 * Date: 10/22/13
 * Time: 3:53 PM
 */
public class BatchInserter {
    private final PostgresqlDatabase db;
    private Map<SampleType,PreparedStatement> insertMap = new HashMap<SampleType, PreparedStatement>();
    private static final Logger Log = Logger.getLogger(BatchInserter.class);

    public BatchInserter(PostgresqlDatabase db) throws SQLException {
        this.db = db;
        prepareStatements();
    }

    private void prepareStatements() throws SQLException {
        insertMap.put(SampleType.ACC, db.connection.prepareStatement("INSERT INTO accelerometer VALUES (?, ?, ?, ?, ?)"));
        insertMap.put(SampleType.LAC, db.connection.prepareStatement("INSERT INTO linear_acceleration VALUES (?, ?, ?, ?, ?)"));
        insertMap.put(SampleType.GPS, db.connection.prepareStatement("INSERT INTO gps VALUES (?, ?, ST_GeomFromText(?,4326))"));
        insertMap.put(SampleType.TAG, db.connection.prepareStatement("INSERT INTO tags VALUES (?, ?, ?)"));
        insertMap.put(SampleType.ACT, db.connection.prepareStatement("INSERT INTO google_activity VALUES (?, ?, ?)"));
        insertMap.put(SampleType.GRA, db.connection.prepareStatement("INSERT INTO gravity VALUES (?, ?, ?, ?, ?)"));
    }

    /**
     * Insert samples form ssf file into database
     *
     *
     * @param db            database to insert samples
     * @return count        number of rows inserted
     * @throws IOException  thrown when error reading the file
     * @throws SQLException thrown on error writing the database
     */
    public static int batchInsertFile(PostgresqlDatabase db, BufferedReader reader) throws IOException, SQLException {

        BatchInserter batchInsert = new BatchInserter(db);

        int count = 0;
        String line = "";

        while ((line = reader.readLine()) != null) {
            try {

                SensorValueInterface SVO = SensorValueFactory.parse(line);

                if (SVO == null) {
                    // sensor type not implemented, yet
                    continue;
                }

                batchInsert.add(SVO);
                if (++count % 1000 == 0) batchInsert.executeBatch();
            } catch (ParseException e) {
                Log.error("Error reading line: " + line,e);
            } catch (SQLException e) {
                Log.error("Error writing to db: " + line,e);
            } catch (NullPointerException e) {
                Log.error("Something odd went wrong:" + line, e);
                break;
            }
        }

        batchInsert.executeBatch();
        batchInsert.close();

        return count;
    }

    public void add(SensorValueInterface svo) throws SQLException {
        SampleType type = svo.getType();
        PreparedStatement ps = insertMap.get(type);

        if (type == SampleType.ACC) {
            AccSensorValue asv = (AccSensorValue) svo;
            ps.setString(1, asv.id);
            ps.setTimestamp(2, new Timestamp(asv.timestamp));
            ps.setFloat(3, asv.x);
            ps.setFloat(4, asv.y);
            ps.setFloat(5, asv.z);
            ps.addBatch();
        } else if (type == SampleType.GPS ){
            GPSSensorValue gsv = (GPSSensorValue) svo;
            ps.setString(1, gsv.id);
            ps.setTimestamp(2, new Timestamp(gsv.timestamp));
            ps.setString(3, "POINT(" + Double.toString(gsv.longitude) + ' ' + Double.toString(gsv.latitude) + ")");
            ps.addBatch();
        } else if (type == SampleType.TAG ) {
            TagSensorValue tsv = (TagSensorValue) svo;
            ps.setString(1, tsv.id);
            ps.setTimestamp(2, new Timestamp(tsv.timestamp));
            ps.setString(3, tsv.tag);
            ps.addBatch();
        } else if (type == SampleType.ACT ) {
            GoogleActivitySensorValue gasv = (GoogleActivitySensorValue) svo;
            ps.setString(1, gasv.id);
            ps.setTimestamp(2, new Timestamp(gasv.timestamp));
            ps.setString(3, gasv.activity);
            ps.addBatch();
        } else if (type == SampleType.LAC) {
            LacSensorValue lasv = (LacSensorValue) svo;
            ps.setString(1, lasv.id);
            ps.setTimestamp(2, new Timestamp(lasv.timestamp));
            ps.setFloat(3, lasv.x);
            ps.setFloat(4, lasv.y);
            ps.setFloat(5, lasv.z);
            ps.addBatch();
        } else if (type == SampleType.GRA) {
            GraSensorValue grasv = (GraSensorValue) svo;
            ps.setString(1, grasv.id);
            ps.setTimestamp(2, new Timestamp(grasv.timestamp));
            ps.setFloat(3, grasv.x);
            ps.setFloat(4, grasv.y);
            ps.setFloat(5, grasv.z);
            ps.addBatch();
        } else {
            // throw new IllegalArgumentException("Sensor Type " + type + " not supported. SVO:" + svo.toSSF());
            Log.warn("Sensortype " + type + "not supported, yet. Found in " + svo.toSSF() );
        }

    }

    public void executeBatch() throws SQLException {
        Log.info("## Execute Batch");
        for (PreparedStatement ps : insertMap.values()) {
            ps.executeBatch();
        }
    }

    public void close() throws SQLException {
        Log.info("## Close Statement");
        for (PreparedStatement ps : insertMap.values()) {
            ps.close();
        }
    }
}
