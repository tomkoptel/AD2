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
import com.paad.ad2.ConfirmDialogFragment;
import com.paad.ad2.R;

import java.util.ArrayList;

public class ToDoListActivity extends FragmentActivity implements NewItemFragment.OnNewItemAddedListener,
        LoaderManager.LoaderCallbacks<Cursor>, ConfirmDialogFragment.ConfirmDialogListener {
    public  static final String ITEM_ID = "itemId";
    public ArrayList<ToDoItem> todoItems;
    private ToDoItemAdapter aa;
    private ConfirmDialogFragment confirmDialog;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist_main);

        confirmDialog = new ConfirmDialogFragment();

        FragmentManager fm = getSupportFragmentManager();
        ToDoListFragment toDoListFragment = (ToDoListFragment) fm.findFragmentById(R.id.TodoListFragment);

        todoItems = new ArrayList<ToDoItem>();
        aa = new ToDoItemAdapter(this, R.layout.todolist_item, todoItems);
        toDoListFragment.setListAdapter(aa);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void showConfirmDialog() {
        FragmentManager fm = getSupportFragmentManager();
        confirmDialog.show(fm, "ConfirmDialog");
    }

    @Override
    public void onDialogPositiveClick() {
        deleteItem();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void deleteItem() {
        String itemId = getIntent().getExtras().getString(ITEM_ID);
        ContentResolver contentResolver = getContentResolver();
        String where = ToDoContentProvider.KEY_ID + "=" + itemId;
        String whereArgs[] = null;

        contentResolver.delete(ToDoContentProvider.CONTENT_URI, where, whereArgs);
    }

    @Override
    public void onNewItemAdded(String newItem) {
        createItem(newItem);
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void createItem(String newItem) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoContentProvider.KEY_TASK, newItem);
        contentResolver.insert(ToDoContentProvider.CONTENT_URI, contentValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(this, ToDoContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        todoItems.clear();

        String keyTask, keyId;
        int keyTaskIndex = cursor.getColumnIndexOrThrow(ToDoContentProvider.KEY_TASK);
        int keyIdIndex = cursor.getColumnIndexOrThrow(ToDoContentProvider.KEY_ID);

        while(cursor.moveToNext()) {
            keyTask = cursor.getString(keyTaskIndex);
            keyId = cursor.getString(keyIdIndex);
            ToDoItem newItem = new ToDoItem(keyId, keyTask);
            todoItems.add(newItem);
        }

        aa.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(0, null, this);
    }
}