package com.paad.earthquake;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NO_CONTENT_OBSERVER = 0;
    private static final int LOADER_ID = 0;
    private ArrayList<Quake> earthquakes = new ArrayList<Quake>();
    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        int layoutId = android.R.layout.simple_list_item_1;
        String[] from = new String[]{EarthquakeProvider.KEY_SUMMARY};
        int[] to = new int[]{android.R.id.text1};

        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(getActivity(), layoutId, null, from, to, NO_CONTENT_OBSERVER);
        setListAdapter(adapter);

        LoaderManager lm = getActivity().getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        refreshEarthquakes();
    }

    public void refreshEarthquakes() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);

        Activity activity = getActivity();
        Intent intent = new Intent(activity, EarthquakeUpdateService.class);
        activity.startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                EarthquakeProvider.KEY_ID,
                EarthquakeProvider.KEY_SUMMARY
        };
        EarthquakeActivity earthquakeActivity = (EarthquakeActivity) getActivity();
        String where = EarthquakeProvider.KEY_MAGNITUDE + " > " + earthquakeActivity.minimumMagnitude;

        return new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI, projection, where, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case LOADER_ID:
                getAdapter().swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getAdapter().swapCursor(null);
    }

    public SimpleCursorAdapter getAdapter() {
        return (SimpleCursorAdapter) getListAdapter();
    }
}
