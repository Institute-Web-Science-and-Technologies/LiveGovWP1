package eu.liveandgov.wp1.server.db_helper.inserter;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.server.db_helper.PostgresqlDatabase;
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
public abstract class AbstractInserter<T extends Item> {

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

    /**
     * Generate String "(?, ?, ?, ?)" with the appropriate number of ?
     * Override to modify perparedStatement to insert samples
     * @return valueString
     */
    protected String getValueString() {
        int commaCount = StringUtils.countMatches(getSchema(),",");
        if (commaCount == 0) throw new IllegalStateException("No fields in Schema");

        StringBuilder valueString = new StringBuilder("(");
        for (int i = 0; i < commaCount; i++) {
            valueString.append("?, ");
        }
        valueString.append("?)");
        return valueString.toString();
    }

    /**
     * Stage sensor values for insertion. Call executeBatch() to write transaction.
     *
     * @param svo - containing sensor values
     * @param tripid - used in database
     * @throws SQLException
     */
    public void batchInsert(T svo, int tripid) throws SQLException {
        insertValues(insertStatement, svo, tripid);
        insertStatement.addBatch();
    }

    /**
     * Store staged sensor samples.
     * @throws SQLException
     */
    public void executeBatch() throws SQLException {
        insertStatement.executeBatch();
    }

    /**
     * Create tables if not exists.
     * @throws SQLException
     */
    public void prepareTable() throws SQLException {
        db.createStatement().execute("CREATE TABLE IF NOT EXISTS " + getTableName() + " " + getSchema() + ";");
    }

    public void dropTable() throws SQLException {
        db.createStatement().execute("DROP TABLE " + getTableName() + ";");
    }

    public void close() throws SQLException {
        insertStatement.close();
    }
}
