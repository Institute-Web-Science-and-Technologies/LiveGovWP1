package eu.liveandgov.wp1.pipeline.impl;

import com.sun.rowset.JdbcRowSetImpl;
import eu.liveandgov.wp1.pipeline.Pipeline;

import javax.sql.RowSetMetaData;
import javax.sql.rowset.JdbcRowSet;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lukas Härtel on 09.05.2014.
 */
public abstract class DB<I, O> extends Pipeline<I, O> {
    /**
     * Scheduler for the automatic closer
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * Automatic closing delay
     */
    private final long delay;

    /**
     * Unit of the automatic closing delay
     */
    private final TimeUnit unit;

    /**
     * URL of the source
     */
    private final String url;

    /**
     * Username in the source domain
     */
    private final String username;

    /**
     * Password in the source domain
     */
    private final String password;

    /**
     * Identity of the catalog
     */
    private final String catalog;

    /**
     * Active row set or null if none active
     */
    private JdbcRowSet activeSet;

    /**
     * Meta-data of the active row set
     */
    private ResultSetMetaData activeMetaData;

    /**
     * The future that represents the automatic closing routine
     */
    private Future<?> activeAutomaticDisconnect;

    /**
     * Constructs the database-pivot
     *
     * @param scheduledExecutorService Scheduler for the automatic closer
     * @param delay                    Automatic closing delay
     * @param unit                     Unit of the automatic closing delay
     * @param url                      URL of the source
     * @param username                 Username in the source domain
     * @param password                 Password in the source domain
     * @param catalog                  Identity of the catalog
     */
    public DB(ScheduledExecutorService scheduledExecutorService, long delay, TimeUnit unit, String url, String username, String password, String catalog) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.delay = delay;
        this.unit = unit;
        this.url = url;
        this.username = username;
        this.password = password;
        this.catalog = catalog;

        activeSet = null;
        activeAutomaticDisconnect = null;
    }

    /**
     * Adds a JDBC driver by its classes name
     *
     * @param className The class name
     * @return Returns true if successfully added
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
     * Keeps an existing connection alive and creates a new automatically terminated connection if not
     */
    private void touch() {
        if (activeSet != null) {
            if (activeAutomaticDisconnect != null)
                activeAutomaticDisconnect.cancel(true);

            activeAutomaticDisconnect = scheduledExecutorService.schedule(disconnection, delay, unit);
        } else {
            activeSet = new JdbcRowSetImpl();

            try {
                activeSet.setUrl(url);
                activeSet.setUsername(username);
                activeSet.setPassword(password);
                activeSet.setCommand("SELECT * FROM " + catalog);

                activeMetaData = activeSet.getMetaData();

                activeAutomaticDisconnect = scheduledExecutorService.schedule(disconnection, delay, unit);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Runnable that manages disconnection of the pending connection
     */
    private final Runnable disconnection = new Runnable() {
        @Override
        public void run() {
            activeAutomaticDisconnect = null;
            try {
                activeSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            activeSet = null;
            activeMetaData = null;
        }
    };

    @Override
    public void push(I i) {
        touch();

        try {
            activeSet.moveToInsertRow();

            write(i, activeMetaData, activeSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Should write the instance of i into the result sets current position with the update* methods
     *
     * @param i   The item to write
     * @param set The target set
     * @throws SQLException May throw an SQL exception
     */
    protected abstract void write(I i, ResultSetMetaData metaData, ResultSet set) throws SQLException;

    /**
     * Should read one instance from the result sets current position with the get* methods
     *
     * @param metaData The meta data of the catalog
     * @param set      The set to read from
     * @return
     * @throws SQLException
     */
    protected abstract O read(ResultSetMetaData metaData, ResultSet set) throws SQLException;

    public void download() {
        touch();

        try {
            activeSet.beforeFirst();

            while (activeSet.next()) {
                produce(read(activeMetaData, activeSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
