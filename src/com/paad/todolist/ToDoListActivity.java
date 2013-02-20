package com.paad.todolist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.paad.ad2.R;

import java.util.ArrayList;

public class ToDoListActivity extends FragmentActivity implements NewItemFragment.OnNewItemAddedListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private ArrayList<ToDoItem> todoItems;
    private ToDoItemAdapter aa;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist_main);

        FragmentManager fm = getSupportFragmentManager();
        ToDoListFragment toDoListFragment = (ToDoListFragment) fm.findFragmentById(R.id.TodoListFragment);

        todoItems = new ArrayList<ToDoItem>();
        aa = new ToDoItemAdapter(this, R.layout.todolist_item, todoItems);
        toDoListFragment.setListAdapter(aa);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onNewItemAdded(String newItem) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoContentProvider.KEY_TASK, newItem);
        contentResolver.insert(ToDoContentProvider.CONTENT_URI, contentValues);
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(this, ToDoContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int keyTaskIndex = cursor.getColumnIndexOrThrow(ToDoContentProvider.KEY_TASK);
        todoItems.clear();
        while(cursor.moveToNext()) {
            ToDoItem newItem = new ToDoItem(cursor.getString(keyTaskIndex));
            todoItems.add(newItem);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(0, null, this);
    }
}