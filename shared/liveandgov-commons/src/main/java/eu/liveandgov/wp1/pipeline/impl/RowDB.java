package eu.liveandgov.wp1.pipeline.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lukas Härtel on 10.05.2014.
 */
public class RowDB extends DB<Map<String, Object>, Map<String, Object>> {
    public RowDB(ScheduledExecutorService scheduledExecutorService, long closeConnectionDelay, TimeUnit closeConnectionUnit, long insertDelay, TimeUnit insertUnit, long closeSelectDelay, TimeUnit closeSelectUnit, String url, String username, String password, String table) {
        super(scheduledExecutorService, closeConnectionDelay, closeConnectionUnit, insertDelay, insertUnit, closeSelectDelay, closeSelectUnit, url, username, password, table);
    }

    public RowDB(ScheduledExecutorService scheduledExecutorService, String url, String username, String password, String table) {
        super(scheduledExecutorService, url, username, password, table);
    }

    @Override
    protected Map<String, Object> read(RowReader rowReader) throws SQLException {

        Map<String, Object> result = new TreeMap<String, Object>();
        for (String c : rowReader.available())
            result.put(c, rowReader.read(c));

        return result;
    }

    @Override
    protected void write(Map<String, Object> stringObjectMap, RowWriter rowWriter) throws SQLException {
        for (String c : rowWriter.required())
            if (stringObjectMap.containsKey(c))
                rowWriter.write(c, stringObjectMap.get(c));
    }


//    @Override
//    protected Object[] transform(Object[] objects) {
//        return objects;
//    }
//
//    @Override
//    protected Object[] read(ResultSet resultSet) throws SQLException {
//        int cc = resultSet.getMetaData().getColumnCount();
//
//        Object[] os = new Object[cc];
//        for (int i = 0; i < cc; i++)
//            os[i] = resultSet.getObject(i + 1);
//
//        return os;
//    }
//
//    @Override
//    protected void write(Object[] objects, PreparedStatement insertStatement) throws SQLException {
//        for (int i = 0; i < objects.length; i++) {
//            insertStatement.setObject(i + 1, objects[i]);
//        }
//    }
}
