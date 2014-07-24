package eu.liveandgov.wp1.pipeline.impl;

import com.sun.rowset.JdbcRowSetImpl;
import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.pipeline.Producer;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Deprecated
/**
 * <p>Database producer</p>
 * Created by Lukas Härtel on 24.02.14.
 */
public class LegacyJDBC extends Producer<Map<String, Object>> {
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
     * Creates a new instance with the given values
     *
     * @param url      URL of the source
     * @param username Username in the source domain
     * @param password Password in the source domain
     */
    public LegacyJDBC(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Adds a JDBC driver by its classes name
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

    public void readAll(String cmd) throws SQLException {
        readAll(cmd, null);
    }

    public void readAll(String cmd, Callback<RowSet> configureParameters) throws SQLException {
        final JdbcRowSet source = new JdbcRowSetImpl();
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        source.setCommand(cmd);

        if (configureParameters != null)
            configureParameters.call(source);

        source.execute();

        final ResultSetMetaData metadata = source.getMetaData();

        final int columnCount = metadata.getColumnCount();
        final String[] columnNames = new String[columnCount];

        for (int i = 0; i < columnCount; i++)
            columnNames[i] = metadata.getColumnName(i + 1);

        final Map<String, Object> row = new HashMap<String, Object>();
        while (source.next()) {
            for (int i = 0; i < columnCount; i++)
                row.put(columnNames[i], source.getObject(i + 1));

            produce(row);
        }

        source.close();
    }
}
