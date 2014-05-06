package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.shared.sensors.sensor_value_objects.GPSSensorValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GpsInserter extends AbstractInserter<GPSSensorValue>{

    public GpsInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_gps";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, lonlat GEOGRAPHY(Point))";
    }

    @Override
    protected String getValueString() {
        return "(?, ?, ST_GeomFromText(?,4326))";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, GPSSensorValue gsv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, gsv.timestamp);
        insertStatement.setString(3, "POINT(" + Double.toString(gsv.longitude) + ' ' + Double.toString(gsv.latitude) + ")");
    }

}
