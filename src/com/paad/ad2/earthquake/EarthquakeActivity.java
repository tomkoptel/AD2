package com.paad.ad2.earthquake;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.paad.ad2.R;

import java.util.Locale;

public class EarthquakeActivity extends SherlockFragmentActivity {
    private static final int MENU_PREFERENCES = Menu.FIRST + 1;
    private static final int MENU_UPDATE = Menu.FIRST + 2;
    private static final int SHOW_PREFERENCES = 1;
    private static final String ACTION_BAR_INDEX = "ACTION_BAR_INDEX";

    public int minimumMagnitude;
    public int updateFreq;
    public boolean autoUpdateChecked;
    private TabListener<EarthquakeListFragment> listTabListener;
    private TabListener<EarthquakeMapFragment> mapTabListener;
    private String localization;

    public void onCreate(Bundle savedInstanceState) {
        setUpLanguage();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_main);

        initActionBar();
        updateFromPreferences();
    }

    private void setUpLanguage() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        localization = prefs.getString(PreferencesActivity.PREF_LANGUAGE, "en");

        Locale locale = new Locale(localization);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void initActionBar() {
        if (isTabletLayout()) return;

        ActionBar actionBar = getSupportActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab listTab = actionBar.newTab();

        listTabListener = new TabListener<EarthquakeListFragment>(this, R.id.EarthquakeFragmentContainer, EarthquakeListFragment.class);
        listTab.setText(R.string.list)
                .setContentDescription(R.string.list_description)
                .setTabListener(listTabListener);

        actionBar.addTab(listTab);

        ActionBar.Tab mapTab = actionBar.newTab();

        mapTabListener = new TabListener<EarthquakeMapFragment>(this, R.id.EarthquakeFragmentContainer, EarthquakeMapFragment.class);
        mapTab.setText(R.string.map)
                .setContentDescription(R.string.map_description)
                .setTabListener(mapTabListener);

        actionBar.addTab(mapTab);
    }

    private boolean isTabletLayout() {
        View fragmentContainer = findViewById(R.id.EarthquakeFragmentContainer);
        return (fragmentContainer == null);
    }

    private void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        minimumMagnitude =
                Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq =
                Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
        localization = prefs.getString(PreferencesActivity.PREF_LANGUAGE, "en");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.menu_refresh): {
                startService(new Intent(this, EarthquakeUpdateService.class));
                break;
            }
            case (R.id.menu_preferences): {
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
        if (requestCode == SHOW_PREFERENCES) {
            updateFromPreferences();
            startService(new Intent(this, EarthquakeUpdateService.class));
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!isTabletLayout()) {
            int actionBarIndex = getSupportActionBar().getSelectedTab().getPosition();

            SharedPreferences.Editor editor = getPreferences(Activity.MODE_PRIVATE).edit();
            editor.putInt(ACTION_BAR_INDEX, actionBarIndex);
            editor.apply();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (mapTabListener.fragment != null)
                ft.detach(mapTabListener.fragment);
            if (listTabListener.fragment != null)
                ft.detach(listTabListener.fragment);
            ft.commit();
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstance) {
        super.onRestoreInstanceState(savedInstance);

        if (isTabletLayout()) return;
        FragmentManager fm = getSupportFragmentManager();
        listTabListener.fragment = fm.findFragmentByTag(EarthquakeListFragment.class.getName());
        mapTabListener.fragment = fm.findFragmentByTag(EarthquakeMapFragment.class.getName());

        SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
        int actionBarIndex = sp.getInt(ACTION_BAR_INDEX, 0);
        getSupportActionBar().setSelectedNavigationItem(actionBarIndex);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isTabletLayout()) return;

        SharedPreferences sp = getPreferences(Activity.MODE_PRIVATE);
        int actionBarIndex = sp.getInt(ACTION_BAR_INDEX, 0);
        getSupportActionBar().setSelectedNavigationItem(actionBarIndex);
    }
}