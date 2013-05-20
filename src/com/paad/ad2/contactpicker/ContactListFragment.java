package com.paad.ad2.contactpicker;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;

public class ContactListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int NO_CONTENT_OBSERVER = 0;
    private static final int LOADER_ID = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] from = new String[]{ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
        int[] to = new int[]{android.R.id.text1};
        int layout = android.R.layout.simple_list_item_1;

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), layout, null, from, to, NO_CONTENT_OBSERVER);
        setListAdapter(adapter);

        LoaderManager lm = getActivity().getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        ContactPickerActivity activity = (ContactPickerActivity) getActivity();
        Cursor cursor = getAdapter().getCursor();

        cursor.moveToPosition(position);
        int rowId = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri outUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, rowId);
        Intent outData = new Intent();
        outData.setData(outUri);

        activity.setResult(Activity.RESULT_OK, outData);
        activity.finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
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
