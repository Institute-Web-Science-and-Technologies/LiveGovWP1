package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Velocity;
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
public class VelInserter extends AbstractInserter<Velocity> {

    public VelInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_velocity";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, velocity FLOAT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, Velocity tsv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, tsv.getTimestamp());
        insertStatement.setFloat(3, tsv.velocity);
    }
}
