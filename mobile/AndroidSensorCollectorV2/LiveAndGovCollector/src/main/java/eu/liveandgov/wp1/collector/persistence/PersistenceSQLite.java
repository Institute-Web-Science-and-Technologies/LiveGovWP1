package eu.liveandgov.wp1.collector.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cehlen on 9/12/13.
 */
public class PersistenceSQLite implements PersistenceInterface {
    private SQLiteHelper dbHelper;
    private SQLiteDatabase database;

    public PersistenceSQLite(Context context) {
        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void save(String value) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.VALUE_NAME, value);
        try {
            database.insertOrThrow(SQLiteHelper.TABLE_NAME, null, values);
        } catch (SQLException e) {
            // TODO: Do some error handling!
        }
    }

    @Override
    public String pull() {
        return null;
    }

    public List<String> readLines(int n) {
        List<String> lines = new ArrayList<String>();
        String[] cols = new String[] { SQLiteHelper.VALUE_NAME };
        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME, cols, null, null,
                null, null, null, String.valueOf(n));
        if (cursor.moveToFirst()) {
            do {
                lines.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        //TODO: Delete records
        return lines;
    }

    @Override
    public int getRecordCount() {
        return database.rawQuery("SELECT COUNT(*) FROM ? ", new String [] { SQLiteHelper.TABLE_NAME} ).getInt(0);
    }


}
