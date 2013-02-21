package com.paad.earthquake;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class EarthquakeProvider extends ContentProvider {
    private static final String AUTHORITY = "com.paad.earthquakeprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/earthquakes");

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longitude";
    public static final String KEY_MAGNITUDE = "magnitude";
    public static final String KEY_LINK = "link";

    private EarthquakeDatabaseHelper dbHelper;

    private static final int QUAKES = 1;
    private static final int QUAKE_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "earthquakes", QUAKES);
        uriMatcher.addURI(AUTHORITY, "earthquakes/#", QUAKE_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new EarthquakeDatabaseHelper(context,
                EarthquakeDatabaseHelper.DATABASE_NAME, null,
                EarthquakeDatabaseHelper.DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(EarthquakeDatabaseHelper.DATABASE_TABLE);

        switch (uriMatcher.match(uri)) {
            case QUAKE_ID:
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }

        String orderBy;
        if (TextUtils.isEmpty(sort)) {
            orderBy = KEY_DATE;
        } else {
            orderBy = sort;
        }

        Cursor cursor = qb.query(database, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                return "vnd.android.cursor.dir/vnd.paad.earthquake";
            case QUAKE_ID:
                return "vnd.android.cursor.item/vnd.paad.earthquake";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri _uri, ContentValues _initialValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long rowID = database.insert(
                EarthquakeDatabaseHelper.DATABASE_TABLE, "quake", _initialValues);
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                count = database.delete(
                        EarthquakeDatabaseHelper.DATABASE_TABLE, where, whereArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(
                        EarthquakeDatabaseHelper.DATABASE_TABLE,
                        KEY_ID + "=" + segment
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                count = database.update(EarthquakeDatabaseHelper.DATABASE_TABLE,
                        values, where, whereArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.update(
                        EarthquakeDatabaseHelper.DATABASE_TABLE, values,
                        KEY_ID + "=" + segment
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
