package eu.liveandgov.wp1.server.db_helper;

import eu.liveandgov.wp1.server.db_helper.inserter.AbstractInserter;
import eu.liveandgov.wp1.server.db_helper.inserter.AccInserter;
import eu.liveandgov.wp1.server.db_helper.inserter.GpsInserter;
import eu.liveandgov.wp1.server.db_helper.inserter.TagInserter;
import eu.liveandgov.wp1.server.sensor_helper.SampleType;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueFactory;
import eu.liveandgov.wp1.server.sensor_helper.SensorValueInterface;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: hartmann
 * Date: 10/22/13
 * Time: 3:53 PM
 */
public class BatchInserter {
    private static PostgresqlDatabase db;
    private Map<SampleType,AbstractInserter> insertMap = new HashMap<SampleType, AbstractInserter>();
    private static final Logger Log = Logger.getLogger(BatchInserter.class);

    private AccInserter accInserter;
    private TagInserter tagInserter;
    private GpsInserter gpsInserter;

    public static final String VALUE_STOP_RECORDING = "STOP_RECORDING";
    public static final String VALUE_START_RECORDING = "START_RECORDING";

    public BatchInserter(PostgresqlDatabase db) throws SQLException {
        this.db = db;

        accInserter = new AccInserter(db);
        tagInserter = new TagInserter(db);
        gpsInserter = new GpsInserter(db);

    }

    private static enum ParsingState {
        STOPPED,
        RUNNING,
        INIT
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

        String lastUserId = "";
        long lastTimestamp = -1;

        int tripId = -1;

        ParsingState state = ParsingState.INIT;


        while ((line = reader.readLine()) != null) {
            try {
                SensorValueInterface SVO = SensorValueFactory.parse(line);

                if (SVO == null) {
                    // sensor type not implemented, yet
                    continue;
                }

                // User ID changes (e.g. first sample)
                if (! lastUserId.equals(SVO.getUserId()) || isStartRecording(SVO)) {
                    tripId = generateNewTripId(SVO);
                    lastUserId = SVO.getUserId();
                    state = ParsingState.RUNNING;
                }

                if (isStopRecording(SVO)) {
                    setStopTime(tripId, SVO.getTimestamp());
                    state = ParsingState.STOPPED;
                }

                if (state == ParsingState.RUNNING){

                    // generate new tripid if gap is too large.
                    final int max_delay_ms = 1000 * 60 * 5;
                    if (Math.abs(SVO.getTimestamp() - lastTimestamp) > max_delay_ms) {
                        Log.debug("Generating new trip_id");
                        tripId = generateNewTripId(SVO);
                    }

                    batchInsert.add(SVO, tripId);
                    if (++count % 1000 == 0) batchInsert.executeBatch();
                }

                lastTimestamp = SVO.getTimestamp();

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

    private static void setStopTime(int tripId, long timestamp) throws SQLException {
        PreparedStatement ps = db.connection.prepareStatement("UPDATE trip SET stop_ts = ? WHERE trip_id = ?");
        ps.setLong(1, timestamp);
        ps.setInt(2, tripId);
        ps.execute();
    }

    private static int generateNewTripId(SensorValueInterface svo) throws SQLException {
        PreparedStatement ps = db.connection.prepareStatement("INSERT into trip (user_id, start_ts, stop_ts) VALUES (?,?,?) RETURNING trip_id");
        ps.setString(1, svo.getUserId());
        ps.setLong(2, svo.getTimestamp());
        ps.setLong(3, svo.getTimestamp());
        ps.execute();
        ResultSet results = ps.getResultSet();
        results.next();
        return results.getInt(1);
    }

    private static boolean isStartRecording(SensorValueInterface svo) {
        if (svo.getType() != SampleType.TAG) return false;
        TagSensorValue tsv = (TagSensorValue) svo;
        return VALUE_START_RECORDING.equals(tsv.getTag().replace("\"", ""));
    }

    private static boolean isStopRecording(SensorValueInterface svo) {
        if (svo.getType() != SampleType.TAG) return false;
        TagSensorValue tsv = (TagSensorValue) svo;
        return VALUE_STOP_RECORDING.equals(tsv.getTag().replace("\"",""));
    }

    public void add(SensorValueInterface svo, int tripId) throws SQLException {
        SampleType type = svo.getType();

        if (type == SampleType.ACC) {
            accInserter.batchInsert((AccSensorValue) svo, tripId);
        } else if (type == SampleType.GPS ){
            gpsInserter.batchInsert((GPSSensorValue) svo, tripId);
        } else if (type == SampleType.TAG ) {
            tagInserter.batchInsert((TagSensorValue) svo, tripId);
        } else if (type == SampleType.ACT ) {
            // TODO
        } else if (type == SampleType.LAC) {
            // TODO
        } else if (type == SampleType.GRA) {
            // TODO
        } else {
            // throw new IllegalArgumentException("Sensor Type " + type + " not supported. SVO:" + svo.toSSF());
            Log.warn("Sensortype " + type + "not supported, yet. Found in " + svo.toSSF() );
        }

    }

    public void executeBatch() throws SQLException {
        Log.info("## Execute Batch");

        accInserter.executeBatch();
        tagInserter.executeBatch();
        gpsInserter.executeBatch();
    }

    public void close() throws SQLException {
        Log.info("## Close Statement");

        accInserter.close();
        tagInserter.close();
        gpsInserter.close();
    }
}
