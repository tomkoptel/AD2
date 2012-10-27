package com.paad.ad2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.paad.compass.CompassActivity;
import com.paad.todolist.TodoListActivity;

import java.util.ArrayList;

public class StartUpActivity extends Activity implements ListView.OnItemClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView myListView = (ListView) findViewById(R.id.mainListView);
        myListView.setOnItemClickListener(this);

        ArrayList<String> list = new ArrayList<String>();
        list.add("Todo List example");
        list.add("Compass example");

        ArrayAdapter<String> aa;
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);
        myListView.setAdapter(aa);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Class activityToLaunch = null;
        switch (i){
            case (0): activityToLaunch = TodoListActivity.class; break;
            case (1): activityToLaunch = CompassActivity.class; break;
            default: activityToLaunch = TodoListActivity.class; break;
        }
        Intent intent = new Intent(StartUpActivity.this, activityToLaunch);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
