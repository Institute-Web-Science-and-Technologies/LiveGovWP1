package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.CallbackSet;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Database-pivot for storing and loading items of the specified type</p>
 * <p>Auto-incremented columns are usually neglected but can be forced write-required by specifying {@link #manual}</p>
 * <p>Auto-generated keys are available by registring a listener to newly generated keys at {@link #keysGenerated}</p>
 * Created by Lukas Härtel on 09.05.2014.
 *
 * @param <I> The type of the ingoing values
 * @param <O> The type of the outgoing values
 */
public abstract class DB<I, O> extends Pipeline<I, O> {
    /**
     * Interface for writing a row
     */
    public static interface RowWriter {
        /**
         * The required columns
         *
         * @return Returns an unmodifiable set of column names
         */
        public Set<String> required();

        /**
         * Writes the column with the given value
         *
         * @param column The column to write, needs to exist
         * @param value  The value to write
         * @throws SQLException An SQL exception that may be thrown on writing
         */
        public void write(String column, Object value) throws SQLException;
    }

    /**
     * Interface for reading a row
     */
    public static interface RowReader {
        /**
         * The available columns
         *
         * @return Returns an unmodifiable set of column names
         */
        public Set<String> available();

        /**
         * Reads the columns value
         *
         * @param column The column to read, needs to exist
         * @throws SQLException An SQL exception that may be thrown on reading
         */
        public Object read(String column) throws SQLException;
    }

    /**
     * Reasonable default for the connection timeout
     */
    public static final long DEFAULT_CONNECTION_DELAY = 10;
    /**
     * Reasonable default for the connection timeout
     */
    public static final TimeUnit DEFAULT_CONNECTION_UNIT = TimeUnit.SECONDS;

    /**
     * Reasonable default for the insertion timeout
     */
    public static final long DEFAULT_INSERT_DELAY = 25;

    /**
     * Reasonable default for the insertion timeout
     */
    public static final TimeUnit DEFAULT_INSERT_UNIT = TimeUnit.MILLISECONDS;

    /**
     * Reasonable default for the selection timeout
     */
    public static final long DEFAULT_SELECT_DELAY = 200;

    /**
     * Reasonable default for the selection timeout
     */
    public static final TimeUnit DEFAULT_SELECT_UNIT = TimeUnit.MILLISECONDS;

    /**
     * Reasonable default for the auto-insert count
     */
    public static final long DEFAULT_AUTO_INSERT_COUNT = 8192;

    /**
     * The scheduler handling the delaying operations
     */
    public final ScheduledExecutorService scheduledExecutorService;

    /**
     * The time the connection is kept open if no operation occurs
     */
    public final long closeConnectionDelay;

    /**
     * The unit of {@link #closeConnectionDelay}
     */
    public final TimeUnit closeConnectionUnit;

    /**
     * The time inserts are withheld if no operations occur
     */
    public final long insertDelay;

    /**
     * The unit of {@link #insertDelay}
     */
    public final TimeUnit insertUnit;

    /**
     * The time selects for read are kept open if no requests occur
     */
    public final long closeSelectDelay;

    /**
     * The unit of {@link #closeSelectDelay}
     */
    public final TimeUnit closeSelectUnit;

    /**
     * The URL of the database
     */
    public final String url;

    /**
     * The username to use at the database
     */
    public final String username;

    /**
     * The password to use for the database
     */
    public final String password;

    /**
     * The table to access at the database
     */
    public final String table;

    /**
     * The auto-generated columns that will be manually inserted
     */
    public final Set<String> manual;

    /**
     * <p>The callback set that allows applications to listen to keys that have been created upon insertion</p>
     * <p>The conversion for output used by the table mapper is used to convert newly generated rows</p>
     */
    public final CallbackSet<O> keysGenerated;

    /**
     * Automatic insert-execution count
     */
    public long autoInsertCount;

    /**
     * The active connection or null if inactive
     */
    private Connection connection;

    /**
     * Amount of pending inserts for batch
     */
    private int insertPendingCount;

    /**
     * The columns not needed for insertion, usually the key columns
     */
    private Set<String> insertOmittedColumns;

    /**
     * The columns needed for the insertion so excluding auto-incrementing and non read-only
     */
    private Map<String, Integer> insertColumns;

    /**
     * The statement of the input insertion
     */
    private PreparedStatement insertStatement;

    /**
     * The statement of the output selection
     */
    private PreparedStatement selectStatement;

    /**
     * The closing future of the connection
     */
    private Future<?> pendingCloseConnection;

    /**
     * The insertion future of the connection
     */
    private Future<?> pendingInsert;

    /**
     * The closing future of the selection
     */
    private Future<?> pendingCloseSelect;

    /**
     * Constructs a new database-pivot
     *
     * @param scheduledExecutorService The scheduler handling the delaying operations
     * @param closeConnectionDelay     The time the connection is kept open if no operation occurs
     * @param closeConnectionUnit      The unit of {@link #closeConnectionDelay}
     * @param insertDelay              The time inserts are withheld if no operations occur
     * @param insertUnit               The unit of {@link #insertDelay}
     * @param closeSelectDelay         The time selects for read are kept open if no requests occur
     * @param closeSelectUnit          The unit of {@link #closeSelectDelay}
     * @param url                      The URL of the database
     * @param username                 The username to use at the database
     * @param password                 The password to use for the database
     * @param table                    The table to access at the database
     */
    public DB(ScheduledExecutorService scheduledExecutorService, long closeConnectionDelay, TimeUnit closeConnectionUnit, long insertDelay, TimeUnit insertUnit, long closeSelectDelay, TimeUnit closeSelectUnit, String url, String username, String password, String table) {

        this.scheduledExecutorService = scheduledExecutorService;
        this.closeConnectionDelay = closeConnectionDelay;
        this.closeConnectionUnit = closeConnectionUnit;
        this.insertDelay = insertDelay;
        this.insertUnit = insertUnit;
        this.closeSelectDelay = closeSelectDelay;
        this.closeSelectUnit = closeSelectUnit;
        this.url = url;
        this.username = username;
        this.password = password;
        this.table = table;

        manual = new TreeSet<String>();

        keysGenerated = new CallbackSet<O>();

        insertPendingCount = 0;
        autoInsertCount = DEFAULT_AUTO_INSERT_COUNT;

        connection = null;
        selectStatement = null;
        insertStatement = null;
        pendingCloseConnection = null;
        pendingInsert = null;
        pendingCloseSelect = null;
    }

    /**
     * Constructs a new database-pivot with reasonable defaults
     *
     * @param scheduledExecutorService The scheduler handling the delaying operations
     * @param url                      The URL of the database
     * @param username                 The username to use at the database
     * @param password                 The password to use for the database
     * @param table                    The table to access at the database
     */
    public DB(ScheduledExecutorService scheduledExecutorService, String url, String username, String password, String table) {
        this(scheduledExecutorService, DEFAULT_CONNECTION_DELAY, DEFAULT_CONNECTION_UNIT, DEFAULT_INSERT_DELAY, DEFAULT_INSERT_UNIT, DEFAULT_SELECT_DELAY, DEFAULT_SELECT_UNIT, url, username, password, table);
    }


    /**
     * Adds the driver by its class name
     *
     * @param className The class-name of the driver
     * @return Returns true if driver was found and thereby registered
     */
    public boolean addDriver(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Selects all items of the table and produces them if a consumer is attached
     */
    public void load() {
        // If consumer is the empty consumer, don't do it
        if (getConsumer() == Consumer.EMPTY_CONSUMER)
            return;

        try {
            // Acquire the selector
            acquireSelectResult();

            // Execute the selector
            final ResultSet resultSet = selectStatement.executeQuery();

            // Make the row reader
            RowReader rowReader = toRowReader(resultSet);

            // Produce all items
            while (resultSet.next()) {
                produce(read(rowReader));
            }

            // Close the result
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Delay the corresponding lifetime objects
        delayCloseSelect();
        delayCloseConnection();
    }

    /**
     * Updates the given item by inferred key
     *
     * @param i The item to update
     * @return Returns true if changed
     */
    public boolean update(I i) {
        try {
            // Acquire insert statement, this will update column names and indices
            acquireInsertStatement();

            return update(i, new ArrayList<String>(insertOmittedColumns));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the given item by the passed keys
     *
     * @param i    The item to update
     * @param keys The key components of the item
     * @return Returns true if changed
     */
    public boolean update(final I i, final List<String> keys) {
        try {
            // Acquire insert statement, this will update column names and indices
            acquireInsertStatement();

            StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
            stringBuilder.append("UPDATE ");
            stringBuilder.append(table);
            stringBuilder.append(" SET ");

            boolean separate = false;
            for (String c : insertColumns.keySet()) {
                if (separate)
                    stringBuilder.append(", ");

                stringBuilder.append(c);
                stringBuilder.append(" = ?");

                separate = true;
            }

            stringBuilder.append(" WHERE ");

            separate = false;
            for (String c : keys) {
                if (separate)
                    stringBuilder.append(" AND ");

                stringBuilder.append(c);
                stringBuilder.append(" = ?");

                separate = true;
            }

            final PreparedStatement updateStatement = connection.prepareStatement(stringBuilder.toString());

            RowWriter dataWriter = new RowWriter() {
                @Override
                public Set<String> required() {
                    return Collections.unmodifiableSet(insertColumns.keySet());
                }

                @Override
                public void write(String column, Object value) throws SQLException {
                    updateStatement.setObject(insertColumns.get(column), value);
                }
            };

            RowWriter keyWriter = new RowWriter() {
                @Override
                public Set<String> required() {
                    return Collections.unmodifiableSet(new TreeSet<String>(keys));
                }

                @Override
                public void write(String column, Object value) throws SQLException {
                    updateStatement.setObject(insertColumns.size() + 1 + keys.indexOf(column), value);
                }
            };

            write(i, dataWriter);
            write(i, keyWriter);

            return updateStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a result set to a row reader
     *
     * @param resultSet The result set to convert
     * @return Returns a new anonymous row reader
     * @throws SQLException Exception that may be thrown on meta extraction
     */
    private RowReader toRowReader(final ResultSet resultSet) throws SQLException {
        // Make set of available columns
        final Set<String> columns = new TreeSet<String>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
            columns.add(resultSet.getMetaData().getColumnName(i));

        // Make reader for each column
        return new RowReader() {
            @Override
            public Set<String> available() {
                return Collections.unmodifiableSet(columns);
            }

            @Override
            public Object read(String column) throws SQLException {
                return resultSet.getObject(column);
            }
        };
    }


    @Override
    public void push(I i) {
        try {
            // Acquire the insert statement
            acquireInsertStatement();

            // Make row writer
            RowWriter rowWriter = new RowWriter() {
                @Override
                public Set<String> required() {
                    return Collections.unmodifiableSet(insertColumns.keySet());
                }

                @Override
                public void write(String column, Object value) throws SQLException {
                    insertStatement.setObject(insertColumns.get(column), value);
                }
            };

            // Fill up the insert statement
            write(i, rowWriter);

            // Add the item to the batch
            insertStatement.addBatch();
            insertPendingCount++;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Delay the corresponding lifetime objects
        delayPerformInsert();
        delayCloseConnection();

        if (insertPendingCount >= autoInsertCount)
            insertNow();

    }

    /**
     * Reads the current row from the passed result set
     *
     * @param rowReader The reader to read from
     * @return Returns the output object
     * @throws SQLException Thrown from used SQL methods
     */
    protected abstract O read(RowReader rowReader) throws SQLException;

    /**
     * Writes to the prepared statements parameter indices
     *
     * @param i         The element to write
     * @param rowWriter The writer to write to
     * @throws SQLException Thrown from used SQL methods
     */
    protected abstract void write(I i, RowWriter rowWriter) throws SQLException;

    /**
     * Acquires a connection if none present
     *
     * @throws SQLException Thrown from used SQL methods
     */
    private void acquireConnection() throws SQLException {
        // If no connection open, consult the driver manager for one
        if (connection == null)
            connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Delays the closing of an open connection
     */
    private void delayCloseConnection() {
        // If no connection open this method is of no use
        if (connection == null)
            return;

        // If a pending operation is there, cancel it
        if (pendingCloseConnection != null)
            pendingCloseConnection.cancel(true);

        // Schedule the closing
        pendingCloseConnection = scheduledExecutorService.schedule(closeConnection, closeConnectionDelay, closeConnectionUnit);
    }

    /**
     * Acquires a connection and the insert statement if none present
     *
     * @throws SQLException Thrown from used SQL methods
     */
    protected void acquireInsertStatement() throws SQLException {
        // Make sure we have connectivity
        acquireConnection();

        // If statement exists stop execution
        if (insertStatement == null) {
            // Prepare a meta-statement to get the count of columns we have to fill
            PreparedStatement info = connection.prepareStatement("SELECT * FROM " + table + " LIMIT 0;");
            ResultSetMetaData metaData = info.getMetaData();

            // Make a dynamic store for the required column names
            insertOmittedColumns = new TreeSet<String>();
            insertColumns = new TreeMap<String, Integer>();

            // Build the real statement
            StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
            stringBuilder.append("INSERT INTO ");
            stringBuilder.append(table);
            stringBuilder.append("(");

            // Go over each column
            boolean separate = false;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // Skip non-requirements
                if ((metaData.isAutoIncrement(i) && !manual.contains(metaData.getColumnName(i))) || metaData.isReadOnly(i)) {
                    insertOmittedColumns.add(metaData.getColumnName(i));
                    continue;
                }

                // If separation needed, do it
                if (separate)
                    stringBuilder.append(", ");

                // Add name of the required column to the required column names and the statement
                insertColumns.put(metaData.getColumnName(i), insertColumns.size() + 1);
                stringBuilder.append(metaData.getColumnName(i));

                // Set separation required
                separate = true;
            }

            // Close the meta-statement
            info.close();

            // Split
            stringBuilder.append(") VALUES (");

            // Go over the amount of columns
            separate = false;
            for (int i = 1; i <= insertColumns.size(); i++) {
                // If separation needed, do it
                if (separate)
                    stringBuilder.append(", ");

                // Add a corresponding parameter
                stringBuilder.append("?");

                // Set separation required
                separate = true;
            }

            stringBuilder.append(");");

            // Prepare with connection
            insertStatement = connection.prepareStatement(stringBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
        }
    }

    /**
     * Delays the execution and closing of an open insert
     */
    private void delayPerformInsert() {
        // If no insert pending this method is of no use
        if (insertStatement == null)
            return;

        // If a pending operation is there, cancel it
        if (pendingInsert != null)
            pendingInsert.cancel(true);

        // Schedule the execution and closing
        pendingInsert = scheduledExecutorService.schedule(performInsert, insertDelay, insertUnit);
    }


    /**
     * Acquires a connection and the select statement if none present
     *
     * @throws SQLException Thrown from used SQL methods
     */
    private void acquireSelectResult() throws SQLException {
        // Make sure we have connectivity
        acquireConnection();

        // If statement exists stop execution, else prepare the statement
        if (selectStatement == null)
            selectStatement = connection.prepareStatement("SELECT * FROM " + table + ";");
    }

    /**
     * Delays the execution and closing of an open select
     */
    private void delayCloseSelect() {
        // If no select pending this method is of no use
        if (selectStatement == null)
            return;

        // If a pending operation is there, cancel it
        if (pendingCloseSelect != null)
            pendingCloseSelect.cancel(true);

        // Schedule and closing
        pendingCloseSelect = scheduledExecutorService.schedule(closeSelect, closeSelectDelay, closeSelectUnit);
    }

    /**
     * Ignores the timed aggregation and inserts all pending items right now, stopping all pending inserts
     */
    public void insertNow() {
        if (pendingInsert != null) {
            pendingInsert.cancel(true);
            pendingInsert = null;
        }

        executeInsertBatch();
    }

    /**
     * Handler for the connection closer
     */
    private final Runnable closeConnection = new Runnable() {
        @Override
        public void run() {
            try {
                pendingCloseConnection = null;

                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Handler for the insertion executor and closer
     */
    private final Runnable performInsert = new Runnable() {
        @Override
        public void run() {
            pendingInsert = null;

            executeInsertBatch();
        }
    };

    /**
     * Performs the actions on insertion
     */
    private void executeInsertBatch() {
        try {
            // Do execute the batch
            insertStatement.executeBatch();

            // Check if generated keys are required
            if (!keysGenerated.getCallbacks().isEmpty()) {
                // If required, get the keys
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                ResultSetMetaData metaData = generatedKeys.getMetaData();

                // Make the row reader
                RowReader rowReader = toRowReader(generatedKeys);

                // Iterate them
                while (generatedKeys.next()) {
                    // Invoke the callback
                    keysGenerated.call(read(rowReader));
                }
            }

            // Close the statement and reset
            insertStatement.close();
            insertStatement = null;

            insertPendingCount = 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handler for the selection closer
     */
    private final Runnable closeSelect = new Runnable() {
        @Override
        public void run() {

            try {
                pendingCloseSelect = null;

                selectStatement.close();
                selectStatement = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };
}
