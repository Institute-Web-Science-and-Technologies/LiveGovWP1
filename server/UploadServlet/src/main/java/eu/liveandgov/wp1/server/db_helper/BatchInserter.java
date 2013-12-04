package eu.liveandgov.wp1.server.db_helper;

import eu.liveandgov.wp1.server.db_helper.inserter.*;
import eu.liveandgov.wp1.shared.sensors.SampleType;
import eu.liveandgov.wp1.shared.sensors.SensorValueFactory;
import eu.liveandgov.wp1.shared.sensors.sensor_value_objects.SensorValueInterface;
import eu.liveandgov.wp1.shared.sensors.sensor_value_objects.*;
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
    private static final Logger Log = Logger.getLogger(BatchInserter.class);

    private static PostgresqlDatabase db;

    private Map<SampleType,AbstractInserter> inserter = new HashMap<SampleType, AbstractInserter>();

    public static final String VALUE_STOP_RECORDING = "STOP_RECORDING";
    public static final String VALUE_START_RECORDING = "START_RECORDING";

    private static int MAX_TRIPS_PER_FILE = 20;
    private static final int MAX_DELAY_MS = 1000 * 60 * 5;

    public BatchInserter(PostgresqlDatabase db) throws SQLException {
        this.db = db;

        inserter.put(SampleType.ACC, new AccInserter(db));
        inserter.put(SampleType.LAC, new LacInserter(db));
        inserter.put(SampleType.GRA, new GraInserter(db));
        inserter.put(SampleType.TAG, new TagInserter(db));
        inserter.put(SampleType.GPS, new GpsInserter(db));
    }

    private static enum ParsingState {
        STOPPED,
        RUNNING,
        INIT,
        NEW_ID
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

        ParsingState state = ParsingState.INIT;
        int rowCount = 0;

        String line = "";
        String lastUserId = "";
        long lastTimestamp = -1;
        int tripId = -1;

        boolean generateNewTripId = true;
        int userIdChangeCount = 0;

        while ((line = reader.readLine()) != null) {
            try {
                SensorValueInterface SVO = SensorValueFactory.parse(line);

                if (SVO == null) {
                    // sensor type not implemented, yet
                    continue;
                }

                if (isStartRecording(SVO)) {
                    generateNewTripId = true;
                    state = ParsingState.RUNNING;
                    continue; // do not insert "STOP_RECORDING" tag
                }

                if (isStopRecording(SVO)) {
                    setStopTime(tripId, SVO.getTimestamp());
                    state = ParsingState.STOPPED;
                    continue; // do not insert "STOP_RECORDING" tag
                }

                // EXCEPTIONAL BEHAVIOR: long delay
                if (Math.abs(SVO.getTimestamp() - lastTimestamp) > MAX_DELAY_MS) {
                    generateNewTripId = true;
                }

                // EXCEPTIONAL BEHAVIOR: new user ID
                if (! lastUserId.equals(SVO.getUserId())) {
                    lastUserId = SVO.getUserId();
                    generateNewTripId = true;
                }

                if (state == ParsingState.RUNNING){
                    if (generateNewTripId) {
                        if (userIdChangeCount++ > MAX_TRIPS_PER_FILE) throw new IllegalArgumentException("Too many userIdChanges");

                        Log.debug("Generating new trip_id");
                        tripId = generateNewTripId(SVO);
                        generateNewTripId = false;
                    }

                    batchInsert.add(SVO, tripId);
                    if (++rowCount % 1000 == 0) batchInsert.executeBatch();
                }

                lastTimestamp = SVO.getTimestamp();

            } catch (ParseException e) {
                Log.error("Error parsing line: " + line,e);
            } catch (SQLException e) {
                Log.error("Error writing to db: " + line,e);
            } catch (NullPointerException e) {
                Log.error("Something odd went wrong:" + line, e);
                break;
            }
        }

        batchInsert.executeBatch();
        batchInsert.close();

        return rowCount;
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

        if (inserter.keySet().contains(type)) {
            inserter.get(type).batchInsert((AbstractSensorValue) svo, tripId);
        } else {
            Log.warn("Sensortype " + type + "not supported, yet. Found in " + svo.toSSF() );
        }

    }

    public void executeBatch() throws SQLException {
        Log.info("## Execute Batch");

        for (AbstractInserter i : inserter.values()){
            i.executeBatch();
        }
    }

    public void close() throws SQLException {
        Log.info("## Close Statement");

        for (AbstractInserter i : inserter.values()){
            i.close();
        }
    }
}
