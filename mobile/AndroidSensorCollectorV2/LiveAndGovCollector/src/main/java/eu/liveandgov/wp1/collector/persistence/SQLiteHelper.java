package eu.liveandgov.wp1.collector.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cehlen on 9/12/13.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME   = "SensorValues";
    public static final int DATABASE_VERSION   = 2;
    public static final String TABLE_NAME      = "sensorValues";
    public static final String VALUE_NAME      = "value";
    public static final String DATABASE_CREATE = "CREATE table " + TABLE_NAME +
                                                  "( " + VALUE_NAME + " text not null );";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
