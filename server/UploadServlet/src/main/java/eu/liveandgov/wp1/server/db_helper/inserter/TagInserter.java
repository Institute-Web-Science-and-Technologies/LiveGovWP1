package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.AccSensorValue;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.TagSensorValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagInserter extends AbstractInserter<TagSensorValue> {

    public TagInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_tags";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, tag TEXT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, TagSensorValue tsv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, tsv.timestamp);
        insertStatement.setString(3, tsv.tag);
    }
}
