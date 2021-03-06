package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.MagneticField;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class RotInserter extends AbstractMotionValueInserter<MagneticField> {

    public RotInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_rotation";
    }

}
