package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GpsInserter extends AbstractInserter<GPS> {

    public GpsInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_gps";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, lonlat GEOGRAPHY(Point),altitude FLOAT NULL)";
    }

    @Override
    protected String getValueString() {
        return "(?, ?, ST_GeomFromText(?,4326), ?)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, GPS gsv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, gsv.getTimestamp());
        insertStatement.setString(3, "POINT(" + Double.toString(gsv.lon) + ' ' + Double.toString(gsv.lat) + ")");

        if (gsv.alt != null)
            insertStatement.setDouble(4, gsv.alt);
        else
            insertStatement.setNull(4, Types.FLOAT);
    }

}
