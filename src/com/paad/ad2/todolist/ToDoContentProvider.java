package com.paad.ad2.todolist;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ToDoContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.paad.todoprovider";

    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_CREATION_DATE = "creation_date";
    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/todoitems");
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "todoitems", ALLROWS);
        uriMatcher.addURI(AUTHORITY, "todoitems/#", SINGLE_ROW);
    }

    private MySQLiteOpenHelper myOpenHelper;

    @Override
    public boolean onCreate() {
        myOpenHelper = new MySQLiteOpenHelper(getContext(),
                MySQLiteOpenHelper.DATABASE_NAME, null,
                MySQLiteOpenHelper.DATABASE_VERSION);
        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.paad.todos";
            case SINGLE_ROW:
                return "vnd.android.cursor.dir/vnd.paad.todos";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + rowID);
            default:
                break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ")" : "");
            default:
                break;
        }
        if(selection == null)
            selection = "1";

        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();
        String nullColumnHack = null;
        long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE, nullColumnHack, values);

        if(id > -1) {
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);

            getContext().getContentResolver().notifyChange(insertedId, null);

            return insertedId;
        } else {
            return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW :
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ?" AND (" + selection + ")" : "");
            default: break;
        }

        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}
