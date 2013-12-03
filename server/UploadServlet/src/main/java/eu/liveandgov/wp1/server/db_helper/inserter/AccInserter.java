package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.AccSensorValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccInserter extends AbstractInserter<AccSensorValue> {

    public AccInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_accelerometer";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, AccSensorValue asv, int tripid) throws SQLException {
        insertStatement.setInt(1,   tripid);
        insertStatement.setLong(2,  asv.timestamp);
        insertStatement.setFloat(3, asv.x);
        insertStatement.setFloat(4, asv.y);
        insertStatement.setFloat(5, asv.z);
    }

}
