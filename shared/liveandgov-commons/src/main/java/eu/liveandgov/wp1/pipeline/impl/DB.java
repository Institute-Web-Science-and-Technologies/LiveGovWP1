package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.util.LocalBuilder;

import java.sql.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Database-pivot for storing and loading items of the specified type</p>
 * Created by Lukas Härtel on 09.05.2014.
 *
 * @param <I> The type of the ingoing values
 * @param <O> The type of the outgoing values
 */
public abstract class DB<I, O> extends Pipeline<I, O> {
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
     * The active connection or null if inactive
     */
    private Connection connection;

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
     * This method is used to bridge the ingoing elements to the outgoing elements
     *
     * @param i The ingoing element
     * @return Returns the corresponding outgoing element
     */
    protected abstract O transform(I i);

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
            ResultSet resultSet = selectStatement.executeQuery();

            // Produce all items
            while (resultSet.next()) {
                produce(read(resultSet));
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


    @Override
    public void push(I i) {
        try {
            // Acquire the insert statement
            acquireInsertStatement();

            // Fill up the insert statement
            write(i, insertStatement);

            // Add the item to the batch
            insertStatement.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // If  consumer is not the empty consumer, push the transformed inptu
        if (getConsumer() != Consumer.EMPTY_CONSUMER)
            produce(transform(i));

        // Delay the corresponding lifetime objects
        delayPerformInsert();
        delayCloseConnection();
    }

    /**
     * Reads the current row from the passed result set
     *
     * @param resultSet The result set to read from
     * @return Returns the output object
     * @throws SQLException Thrown from used SQL methods
     */
    protected abstract O read(ResultSet resultSet) throws SQLException;

    /**
     * Writes to the prepared statements parameter indices
     *
     * @param i               The element to write
     * @param insertStatement The statement to write to
     * @throws SQLException Thrown from used SQL methods
     */
    protected abstract void write(I i, PreparedStatement insertStatement) throws SQLException;

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
            int cc = info.getMetaData().getColumnCount();
            info.close();

            // Build the real statement
            StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
            stringBuilder.append("INSERT INTO ");
            stringBuilder.append(table);
            stringBuilder.append(" VALUES(");

            if (cc > 0) {
                stringBuilder.append("?");
                for (int i = 1; i < cc; i++)
                    stringBuilder.append(",?");
            }

            stringBuilder.append(");");

            // Prepare with connection
            insertStatement = connection.prepareStatement(stringBuilder.toString());
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

        try {
            insertStatement.executeBatch();
            insertStatement.close();
            insertStatement = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
            try {
                pendingInsert = null;

                insertStatement.executeBatch();
                insertStatement.close();
                insertStatement = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };

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
