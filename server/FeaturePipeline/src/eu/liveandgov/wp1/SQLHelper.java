package eu.liveandgov.wp1;

import eu.liveandgov.wp1.Window.Window;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 10/11/13
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLHelper {

    private PostgresqlDatabase db;
    private PreparedStatement selectDistinctTagsQuery;
    private PreparedStatement selectWindowQuery;
    private PreparedStatement saveFeatureWindowQuery;

    public SQLHelper(String username, String password) {
        Log.Log("Connecting to database.");
        db = new PostgresqlDatabase(username, password);
        try {
            saveFeatureWindowQuery = db.connection.prepareStatement("INSERT INTO feature_values (type, id, startTime, endTime, " +
                    "xMean, yMean, zMean, " +
                    "xVar , yVar , zVar , " +
                    "s2Mean, s2Var, tag )" +
                    " VALUES (?, ?, ?, ?," +
                    "?, ?, ?," +
                    "?, ?, ?," +
                    "?, ?, ?)");
            selectDistinctTagsQuery = db.connection.prepareStatement("SELECT DISTINCT tag FROM raw_training_data WHERE id=?");
            selectWindowQuery = db.connection.prepareStatement("SELECT ts, x, y, z FROM raw_training_data WHERE type=? AND id=? AND ts BETWEEN ? AND ? ORDER BY ts asc");
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Window[] getWindows(String sensorType, String id, String tag, long overlap, Date start, long windowSize) {
        List<Window> windowList = new ArrayList<Window>();
        Timestamp tsStart = new Timestamp(start.getTime());
        Timestamp tsEnd = new Timestamp(start.getTime() + windowSize);

        try {
            while(true) {
                selectWindowQuery.setString(1, sensorType);
                selectWindowQuery.setString(2, id);

                selectWindowQuery.setTimestamp(3, tsStart);
                selectWindowQuery.setTimestamp(4, tsEnd);

                ResultSet result = selectWindowQuery.executeQuery();

                // Stop if we don't get any result back!
                if(!result.next()) break;

                windowList.add(new Window(sensorType, tag, id, result));

                // calculate new window start/endpoint
                tsStart = new Timestamp(tsEnd.getTime() - overlap);
                tsEnd = new Timestamp(tsStart.getTime() + windowSize);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
        return windowList.toArray(new Window[windowList.size()]);
    }

    public ArrayList<String> getAllTagsForId(String id) {
        try {
            selectDistinctTagsQuery.setString(1, id);
            ResultSet resultSet = selectDistinctTagsQuery.executeQuery();
            ArrayList<String> rslt = new ArrayList<String>();
            while(resultSet.next()) {
                rslt.add(resultSet.getString(1));
            }
            return rslt;
        } catch(SQLException e) {
            Log.Log("Error while getAllTagsForId");
            e.printStackTrace();
            return null;
        }
    }

    public void saveFeatureWindow(Window w) {
        try {
            saveFeatureWindowQuery.setString(1, w.getType());
            saveFeatureWindowQuery.setString(2, w.getId());
            saveFeatureWindowQuery.setTimestamp(3, new Timestamp(w.getStartTime().getTime()));
            saveFeatureWindowQuery.setTimestamp(4, new Timestamp(w.getEndTime().getTime()));
            saveFeatureWindowQuery.setFloat(5, w.getXMean());
            saveFeatureWindowQuery.setFloat(6, w.getYMean());
            saveFeatureWindowQuery.setFloat(7, w.getZMean());

            saveFeatureWindowQuery.setFloat(8 , w.getXVar());
            saveFeatureWindowQuery.setFloat(9 , w.getYVar());
            saveFeatureWindowQuery.setFloat(10, w.getZVar());

            saveFeatureWindowQuery.setFloat(11, w.getS2Mean());
            saveFeatureWindowQuery.setFloat(12, w.getS2Var());
            saveFeatureWindowQuery.setString(13, w.getTag());

            saveFeatureWindowQuery.

            if(!saveFeatureWindowQuery.execute()) {
                Log.Log("Save error?");
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
