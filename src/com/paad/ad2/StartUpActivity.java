package com.paad.ad2;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;

public class StartUpActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = getSupportFragmentManager();
        AppListFragment listFragment = (AppListFragment) fm.findFragmentById(R.id.AppListFragment);

        String[] apps = getResources().getStringArray(R.array.apps);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apps);
        listFragment.setListAdapter(aa);
    }
}
