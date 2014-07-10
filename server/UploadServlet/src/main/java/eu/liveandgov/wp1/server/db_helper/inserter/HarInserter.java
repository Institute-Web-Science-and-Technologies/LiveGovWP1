package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HarInserter extends AbstractInserter<Activity> {

    public HarInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_har";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, tag TEXT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, Activity asv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, asv.getTimestamp());
        insertStatement.setString(3, asv.activity);
    }
}
