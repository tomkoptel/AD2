package com.paad.contactpicker;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import com.paad.ad2.AppListFragment;
import com.paad.ad2.R;

public class ContactPicker extends FragmentActivity  {
    private static final int NO_CONTENT_OBSERVER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_contact_picker);

        FragmentManager fm = getSupportFragmentManager();
        ContactListFragment listFragment = (ContactListFragment) fm.findFragmentById(R.id.ContactListFragment);

        String[] from = new String[]{ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
        int[] to = new int[]{android.R.id.text1};
        int layout = android.R.layout.simple_list_item_1;

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, layout, null, from, to, NO_CONTENT_OBSERVER);
        listFragment.setListAdapter(adapter);
    }
}