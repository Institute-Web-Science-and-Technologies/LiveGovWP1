package eu.liveandgov.wp1.database;

import eu.liveandgov.wp1.PostgresqlDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static void connect(String username, String password) {
        db = new PostgresqlDatabase(username, password);
        try {
            getByTagQuery = db.connection.prepareStatement("SELECT tag, ts, x, y, z FROM raw_training_data WHERE tag=? AND id=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getByTag(String tag, String id) {
        try {
            getByTagQuery.setString(1, tag);
            getByTagQuery.setString(2, id);
            return getByTagQuery.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
