package com.paad.contactpicker;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.paad.ad2.R;

public class ContactPicker extends Activity implements AdapterView.OnItemClickListener {
    private Cursor cursor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        String[] from = new String[] {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
        int[] to = new int[] {R.id.itemTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.listitemlayout, cursor, from, to);
        ListView lv = (ListView)findViewById(R.id.mainListView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        cursor.moveToPosition(pos);
        int rowId = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri outUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, rowId);
        Intent outData = new Intent();
        outData.setData(outUri);
        setResult(Activity.RESULT_OK, outData);
        finish();
    }
}