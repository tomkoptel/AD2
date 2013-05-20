package com.paad.ad2.earthquake;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import com.paad.ad2.R;


public class EarthquakeSearchResults extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NO_CONTENT_OBSERVER = 0;
    private static final int LOADER_ID = 0;
    private static final String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";
    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.list_content);

        int layoutId = android.R.layout.simple_list_item_1;
        String[] from = new String[]{EarthquakeProvider.KEY_SUMMARY};
        int[] to = new int[]{android.R.id.text1};

        adapter = new SimpleCursorAdapter(this, layoutId, null, from, to, NO_CONTENT_OBSERVER);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        LoaderManager lm = getSupportLoaderManager();
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
            getSupportLoaderManager().restartLoader(0, args, this);
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
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
