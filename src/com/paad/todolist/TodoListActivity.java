package com.paad.todolist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.paad.ad2.R;

import java.util.ArrayList;

public class TodoListActivity extends FragmentActivity implements EditText.OnKeyListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private ArrayList<String> todoItems;
    private EditText myEditText;
    private ArrayAdapter<String> aa;
    private static final int ADD_NEW_TODO = Menu.FIRST;
    private static final int REMOVE_TODO = Menu.FIRST + 1;
    private ListView myListView;
    private boolean addingNew = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist_main);
        myListView = (ListView) findViewById(R.id.myListView);
        myEditText = (EditText) findViewById(R.id.myEditText);
        myEditText.setOnKeyListener(this);

        todoItems = new ArrayList<String>();
        int resId = R.layout.todolist_item;
        aa = new ArrayAdapter<String>(this, resId, todoItems);
        myListView.setAdapter(aa);

        registerForContextMenu(myListView);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
            if((keyCode == KeyEvent.KEYCODE_ENTER) || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                todoItems.add(0, myEditText.getText().toString());
                aa.notifyDataSetChanged();
                myEditText.setText("");
                cancelAdd();
                return true;
            }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuItem itemAdd = menu.add(0, ADD_NEW_TODO, Menu.NONE, R.string.add_new);
        MenuItem itemRemove = menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);

        itemAdd.setIcon(R.drawable.add_new_item);
        itemRemove.setIcon(R.drawable.remove_item);

        itemAdd.setShortcut('0', 'a');
        itemRemove.setShortcut('1', 'r');

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info){
        super.onCreateContextMenu(menu, view, info);
        menu.setHeaderTitle("Selected To Do Item");
        menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        int idx = myListView.getSelectedItemPosition();
        String removeTitle = getString(addingNew ? R.string.cancel : R.string.remove);

        MenuItem removeItem = menu.findItem(REMOVE_TODO);
        removeItem.setTitle(removeTitle);
        removeItem.setVisible(addingNew || idx > -1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        int index = myListView.getSelectedItemPosition();

        switch (item.getItemId()){
            case (REMOVE_TODO):{
                if(addingNew){
                    cancelAdd();
                } else {
                    removeItem(index);
                }
                return true;
            }
            case (ADD_NEW_TODO):{
                addNewItem();
                return true;
            }
        }

        return false;
    }

    private void cancelAdd() {
        addingNew = false;
        myEditText.setVisibility(View.GONE);
    }

    private void removeItem(int index) {
        todoItems.remove(index);
        aa.notifyDataSetChanged();
    }

    private void addNewItem() {
        addingNew = true;
        myEditText.setVisibility(View.VISIBLE);
        myEditText.requestFocus();
    }

    public void onNewItemAdded(String newItem) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoContentProvider.KEY_TASK, newItem);
        contentResolver.insert(ToDoContentProvider.CONTENT_URI, contentValues);
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        super.onContextItemSelected(item);

        switch (item.getItemId()){
            case (REMOVE_TODO):{
                AdapterView.AdapterContextMenuInfo menuInfo;
                menuInfo = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo());
                int index = menuInfo.position;

                removeItem(index);
                return true;
            }
        }
        return false;
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
//            ToDoItem newItem = new ToDoItem(cursor.getString(keyTaskIndex));
//            todoItems.add(newItem);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(0, null, this);
    }
}