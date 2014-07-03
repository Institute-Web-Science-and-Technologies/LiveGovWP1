package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.Motion;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractMotionValueInserter<T extends Motion> extends AbstractInserter<T>{

    public AbstractMotionValueInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, T msv, int tripId) throws SQLException {
        insertStatement.setInt(1,   tripId);
        insertStatement.setLong(2,  msv.getTimestamp());
        insertStatement.setFloat(3, msv.values[0]);
        insertStatement.setFloat(4, msv.values[1]);
        insertStatement.setFloat(5, msv.values[2]);
    }
}
