package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
import eu.liveandgov.wp1.server.sensor_helper.sensor_value_objects.AbstractSensorValue;
import org.apache.commons.lang.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/3/13
 * Time: 7:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractInserter<T extends AbstractSensorValue> {

    protected final PostgresqlDatabase db;
    private final PreparedStatement insertStatement;

    public AbstractInserter(PostgresqlDatabase db) throws SQLException {
        this.db = db;
        this.insertStatement = getInsertStatement();
        prepareTable();
    }

    /**
     * @return tableName - Name of table, e.g. "sensor_accelerometer"
     */
    protected abstract String getTableName();

    /**
     * @return schema - Schema of table enclosed in brackets, e.g. "(trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT)";
     */
    protected abstract String getSchema();

    /**
     * Insert SVO into prepared statement ps passed as parameter
     * @param insertStatement - prepared statement created by getInsertStatement()
     * @param svo - sensor value object of template type
     * @param tripId
     */
    public abstract void insertValues(PreparedStatement insertStatement, T svo, int tripId) throws SQLException;

    private PreparedStatement getInsertStatement() throws SQLException {
        return db.connection.prepareStatement("INSERT INTO " + getTableName() + " VALUES " + getValueString() + ";");
    }

    protected String getValueString() {
        // generate String "(?, ?, ?, ?)" with the appropriate number of ?

        int commaCount = StringUtils.countMatches(getSchema(),",");
        if (commaCount == 0) throw new IllegalStateException("No fields in Schema");

        StringBuilder valueString = new StringBuilder("(");
        for (int i = 0; i < commaCount; i++) {
            valueString.append("?, ");
        }
        valueString.append("?)");
        return valueString.toString();
    }

    public void batchInsert(T svo, int tripid) throws SQLException {
        insertValues(insertStatement, svo, tripid);
        insertStatement.addBatch();
    }

    public void prepareTable() throws SQLException {
        db.createStatement().execute("CREATE TABLE IF NOT EXISTS " + getTableName() + " " + getSchema() + ";");
    }

    public void executeBatch() throws SQLException {
        insertStatement.executeBatch();
    }

    public void close() throws SQLException {
        insertStatement.close();
    }
}
