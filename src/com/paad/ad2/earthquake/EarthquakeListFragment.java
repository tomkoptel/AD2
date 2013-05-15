package com.paad.ad2.earthquake;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NO_CONTENT_OBSERVER = 0;
    private static final int LOADER_ID = 0;

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        int layoutId = android.R.layout.simple_list_item_1;
        String[] from = new String[]{EarthquakeProvider.KEY_SUMMARY};
        int[] to = new int[]{android.R.id.text1};

        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(getActivity(), layoutId, null, from, to, NO_CONTENT_OBSERVER);
        setListAdapter(adapter);

        LoaderManager lm = getActivity().getLoaderManager();
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
