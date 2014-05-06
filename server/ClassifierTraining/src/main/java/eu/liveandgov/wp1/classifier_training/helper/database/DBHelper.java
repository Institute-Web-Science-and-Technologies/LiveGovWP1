package eu.liveandgov.wp1.classifier_training.helper.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 12/11/13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class DBHelper {
    private static PostgresqlDatabase db;

    private static PreparedStatement getByTagQuery;
    private static PreparedStatement getTags;


    public static void connect(String username, String password) {
        db = new PostgresqlDatabase(username, password);
        try {
            getByTagQuery = db.connection.prepareStatement("SELECT id, tag, ts, x, y, z FROM raw_training_data WHERE tag=? AND id=? ORDER BY ts");
            getTags = db.connection.prepareStatement("SELECT DISTINCT tag FROM raw_training_data WHERE id=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getByTagId(String tag, String id) {
        try {
            getByTagQuery.setString(1, tag);
            getByTagQuery.setString(2, id);
            return getByTagQuery.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAllIds() {
        try {
            PreparedStatement getIds = db.connection.prepareStatement("SELECT DISTINCT id FROM raw_training_data");
            ResultSet rs = getIds.executeQuery();
            List<String> ids = new ArrayList<String>();
            while(rs.next()) {
                ids.add(rs.getString("id"));
            }
            return ids;
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static List<String> getTagsForId(String id) {
        try {
            getTags.setString(1, id);
            ResultSet rs = getTags.executeQuery();
            List<String> tags = new ArrayList<String>();
            while(rs.next()) {
                tags.add(rs.getString("tag"));
            }
            return tags;
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
