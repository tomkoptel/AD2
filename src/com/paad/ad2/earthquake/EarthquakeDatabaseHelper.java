package com.paad.ad2.earthquake;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EarthquakeDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "EarthquakeProvider";
    public static final String DATABASE_NAME = "earthquakes.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE = "earthquakes";
    private static final String DATABASE_CREATE =
    "create table " + DATABASE_TABLE + " ("
            + EarthquakeProvider.KEY_ID + " integer primary key autoincrement, "
            + EarthquakeProvider.KEY_DATE + " INTEGER, "
            + EarthquakeProvider.KEY_DETAILS + " TEXT, "
            + EarthquakeProvider.KEY_SUMMARY + " TEXT, "
            + EarthquakeProvider.KEY_LOCATION_LAT + " FLOAT, "
            + EarthquakeProvider.KEY_LOCATION_LNG + " FLOAT, "
            + EarthquakeProvider.KEY_MAGNITUDE + " FLOAT, "
            + EarthquakeProvider.KEY_LINK + " TEXT);";

    private SQLiteDatabase earthquakeDB;

    public EarthquakeDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public EarthquakeDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String format = "Upgrading from version %d to %d, which will destroy all old data";
        Log.w(TAG, String.format(format, oldVersion, newVersion));
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
