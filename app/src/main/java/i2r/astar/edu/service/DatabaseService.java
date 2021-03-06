package i2r.astar.edu.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import i2r.astar.edu.database.DatabaseContext;

import static i2r.astar.edu.global.Constant.*;

/**
 * Created by dashm on 8/6/15.
 */
public class DatabaseService extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DBNAME = "dataloc_v1.0";

    public DatabaseService(DatabaseContext context) {
        super(context, DBNAME, null, VERSION);
    }

    private void createDatabase(SQLiteDatabase db) {

        if (!isTableExists(SQL_TABLE_LOCATION, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_LOCATION +
                    SQL_OPEN_BRACKET + SQL_PK_ID + SQL_COMMA +
                     SQL_LAT + SQL_REAL + SQL_COMMA + SQL_LON + SQL_REAL +
                    SQL_COMMA + SQL_DATE + SQL_TIMESTAMP + SQL_COMMA +
                    SQL_LOCATION_PROVIDER + SQL_TEXT + SQL_COMMA +
                    SQL_LOCATION_ACCURACY + SQL_REAL + SQL_COMMA + SQL_LOCATION_ALTITUDE
                    + SQL_REAL +
                    SQL_COMMA + SQL_LOCATION_SPEED
                    + SQL_REAL + SQL_COMMA + SQL_LOCATION_BEARING
                    + SQL_REAL +
                    SQL_CLOSE_BRACKET);
        }
        if (!isTableExists(SQL_TABLE_SENSOR, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_SENSOR + SQL_OPEN_BRACKET + SQL_PK_ID +
                    SQL_COMMA +
                    SQL_DATE + SQL_TIMESTAMP + SQL_COMMA +
                    SQL_SENSOR_X +
                    SQL_REAL + SQL_COMMA + SQL_SENSOR_Y + SQL_REAL + SQL_COMMA + SQL_SENSOR_Z +
                    SQL_REAL + SQL_COMMA + SQL_SENSOR_MAG + SQL_REAL + SQL_CLOSE_BRACKET);
        }

        if (!isTableExists(SQL_TABLE_STAY, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_STAY + SQL_OPEN_BRACKET+ SQL_PK_ID + SQL_COMMA +
                     SQL_LAT + SQL_REAL + SQL_COMMA
                     + SQL_LON + SQL_REAL + SQL_COMMA +
                    SQL_STAY_START + SQL_TIMESTAMP + SQL_COMMA +
                    SQL_STAY_END + SQL_TIMESTAMP + SQL_COMMA + SQL_LOCATION_ACCURACY +
                    SQL_REAL +
                    SQL_CLOSE_BRACKET);
        }
        if (!isTableExists(SQL_TABLE_LIGHT, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_LIGHT + SQL_OPEN_BRACKET + SQL_PK_ID + SQL_COMMA +
                    SQL_DATE +
                    SQL_TIMESTAMP + SQL_COMMA + SQL_RECORDVALUE + SQL_REAL + SQL_CLOSE_BRACKET);
        }

        if (!isTableExists(SQL_TABLE_SOUND, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_SOUND + SQL_OPEN_BRACKET + SQL_PK_ID + SQL_COMMA +
                    SQL_DATE +
                    SQL_TIMESTAMP + SQL_COMMA + SQL_RECORDVALUE + SQL_REAL + SQL_CLOSE_BRACKET);
        }

        if (!isTableExists(SQL_TABLE_STEP, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_STEP + SQL_OPEN_BRACKET +
                    SQL_PK_ID +
                    SQL_COMMA + SQL_DATE +
                    SQL_TIMESTAMP + SQL_COMMA +
                    SQL_RECORDVALUE + SQL_REAL + SQL_CLOSE_BRACKET);
        }

        if (!isTableExists(SQL_TABLE_ERROR, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_ERROR + SQL_OPEN_BRACKET + SQL_PK_ID + SQL_COMMA +
                    SQL_DATE +
                    SQL_TIMESTAMP +
                    SQL_COMMA + SQL_ERROR_MESSAGE + SQL_TEXT + SQL_COMMA +
                    SQL_ERROR_SOURCE + SQL_TEXT + SQL_CLOSE_BRACKET);
        }

        if (!isTableExists(SQL_TABLE_MODE, db)){
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_MODE + SQL_OPEN_BRACKET + SQL_PK_ID + SQL_COMMA +
                    SQL_DATE + SQL_TIMESTAMP + SQL_COMMA + SQL_TRANSPORT_MODE + SQL_REAL +
                    SQL_CLOSE_BRACKET);
        }

        if (!isTableExists(SQL_TABLE_AZIMUTH, db)) {
            db.execSQL(SQL_CREATE_TABLE + SQL_TABLE_AZIMUTH + SQL_OPEN_BRACKET + SQL_PK_ID + SQL_COMMA +
                    SQL_DATE +
                    SQL_TIMESTAMP + SQL_COMMA + SQL_RECORDVALUE + SQL_REAL + SQL_CLOSE_BRACKET);
        }
    }

    //http://stackoverflow.com/questions/3058909/how-does-one-check-if-a-table-exists-in-an
    // -android-sqlite-database
    public boolean isTableExists(String tableName, SQLiteDatabase database) {

        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where " +
                "tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
