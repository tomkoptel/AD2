package com.paad.earthquake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import com.paad.ad2.R;

public class EarthquakeActivity extends FragmentActivity {
    private static final int MENU_PREFERENCES = Menu.FIRST+1;
    private static final int MENU_UPDATE = Menu.FIRST+2;
    private static final int SHOW_PREFERENCES = 1;

    public int minimumMagnitude;
    public int updateFreq;
    public boolean autoUpdateChecked;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (MENU_PREFERENCES): {
                Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
                        PreferencesActivity.class : FragmentPreferences.class;
                Intent intent = new Intent(this, c);
                startActivityForResult(intent, SHOW_PREFERENCES);
                return true;
            }
        }
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFERENCES)
            updateFromPreferences();

        FragmentManager fm = getSupportFragmentManager();
        final EarthquakeListFragment earthquakeList =(EarthquakeListFragment)
                        fm.findFragmentById(R.id.EarthquakeListFragment);
        Thread t = new Thread(new Runnable() {
            public void run() {
                earthquakeList.refreshEarthquakes();
            }
        });
        t.start();
    }

    private void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        minimumMagnitude =
                Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq =
                Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
    }
}