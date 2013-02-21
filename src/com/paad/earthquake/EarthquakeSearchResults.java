package com.paad.earthquake;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;

public class EarthquakeSearchResults extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NO_CONTENT_OBSERVER = 0;
    private static final int LOADER_ID = 0;
    private static final String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        int layoutId = android.R.layout.simple_list_item_1;
        String[] from = new String[]{EarthquakeProvider.KEY_SUMMARY};
        int[] to = new int[]{android.R.id.text1};

        SimpleCursorAdapter adapter;
        adapter = new SimpleCursorAdapter(this, layoutId, null, from, to, NO_CONTENT_OBSERVER);
        setListAdapter(adapter);

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(getIntent());
    }

    private void parseIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Bundle args = new Bundle();
            args.putString(QUERY_EXTRA_KEY, searchQuery);
            getLoaderManager().restartLoader(0, args, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "0";
        if (args != null) {
            query = args.getString(QUERY_EXTRA_KEY);
        }
        String[] projection = {
                EarthquakeProvider.KEY_ID,
                EarthquakeProvider.KEY_SUMMARY
        };

        String where = EarthquakeProvider.KEY_SUMMARY + " LIKE \"%" + query + "%\"";
        String[] whereArgs = null;
        String sortOrder = EarthquakeProvider.KEY_SUMMARY + " COLLATE LOCALIZED ASC";

        return new CursorLoader(this, EarthquakeProvider.CONTENT_URI, projection, where, whereArgs, sortOrder);
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
