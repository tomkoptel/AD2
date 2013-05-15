package com.paad.ad2.todolist;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "todoDatabase.db";
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_TABLE = "todoItemTable";

    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE +" ("
            + ToDoContentProvider.KEY_ID + " integer primary key autoincrement, "
            + ToDoContentProvider.KEY_TASK + " text not null, "
            + ToDoContentProvider.KEY_CREATION_DATE + "long);";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String format = "Upgrading from version %d to %d, which will destroy all old data";
        Log.w("TaskDBAdapter", String.format(format, oldVersion, newVersion));
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
