package com.paad.todolist;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class StartUpActivity extends Activity implements EditText.OnKeyListener {
    private ArrayList<String> todoItems;
    private EditText myEditText;
    private ArrayAdapter<String> aa;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView myListView = (ListView) findViewById(R.id.myListView);
        myEditText = (EditText) findViewById(R.id.myEditText);
        myEditText.setOnKeyListener(this);

        todoItems = new ArrayList<String>();
        int resId = R.layout.todolist_item;
        aa = new ArrayAdapter<String>(this, resId, todoItems);
        myListView.setAdapter(aa);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
            if((keyCode == KeyEvent.KEYCODE_ENTER) || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                todoItems.add(0, myEditText.getText().toString());
                aa.notifyDataSetChanged();
                myEditText.setText("");
                return true;
            }
        return false;
    }
}
