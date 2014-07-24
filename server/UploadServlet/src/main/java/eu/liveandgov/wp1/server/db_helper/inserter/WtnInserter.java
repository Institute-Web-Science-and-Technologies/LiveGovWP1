package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Waiting;
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
public class WtnInserter extends AbstractInserter<Waiting> {

    public WtnInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_waiting";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, key TEXT, duration BIGINT, at TEXT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, Waiting tsv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, tsv.getTimestamp());
        insertStatement.setString(3, tsv.key);
        insertStatement.setLong(4, tsv.duration);
        insertStatement.setString(5, tsv.at);
    }
}
