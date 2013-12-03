package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.shared.sensor_value_objects.AbstractMotionSensorValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractMotionValueInserter<T extends AbstractMotionSensorValue> extends AbstractInserter<T>{

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
        insertStatement.setLong(2,  msv.timestamp);
        insertStatement.setFloat(3, msv.x);
        insertStatement.setFloat(4, msv.y);
        insertStatement.setFloat(5, msv.z);
    }
}
