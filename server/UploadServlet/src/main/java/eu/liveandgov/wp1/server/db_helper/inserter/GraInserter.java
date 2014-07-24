package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.LinearAcceleration;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 *
 */
public class GraInserter extends AbstractMotionValueInserter<LinearAcceleration> {

    public GraInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_gravity";
    }

}
