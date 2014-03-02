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

/**
 * Created by Lukas Härtel on 24.02.14.
 */
public class JDBC extends Producer<Map<String, Object>> {
    private final String url;

    private final String username;

    private final String password;

    public JDBC(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
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
            columnNames[i] = metadata.getColumnName(i);

        final Map<String, Object> row = new HashMap<String, Object>();
        while (source.next()) {
            for (int i = 0; i < columnCount; i++)
                row.put(columnNames[i], source.getObject(i));

            produce(row);
        }

        source.close();
    }
}
