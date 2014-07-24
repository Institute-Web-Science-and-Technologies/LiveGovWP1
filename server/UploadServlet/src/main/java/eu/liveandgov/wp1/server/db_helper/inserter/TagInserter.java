package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.impl.Tag;
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
public class TagInserter extends AbstractInserter<Tag> {

    public TagInserter(PostgresqlDatabase db) throws SQLException {
        super(db);
    }

    @Override
    protected String getTableName() {
        return "sensor_tags";
    }

    @Override
    protected String getSchema() {
        return "(trip_id INT, ts BIGINT, tag TEXT)";
    }

    @Override
    public void insertValues(PreparedStatement insertStatement, Tag tsv, int tripId) throws SQLException {
        insertStatement.setInt(1, tripId);
        insertStatement.setLong(2, tsv.getTimestamp());
        insertStatement.setString(3, tsv.tag);
    }
}
