package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.shared.sensors.sensor_value_objects.GoogleActivitySensorValue;
import eu.liveandgov.wp1.shared.sensors.sensor_value_objects.TagSensorValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HarInserter extends AbstractInserter<GoogleActivitySensorValue> {

    public HarInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "har_annotation";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, tag TEXT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, GoogleActivitySensorValue asv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, asv.timestamp);
        insertStatement.setString(3, asv.activity);
    }
}
