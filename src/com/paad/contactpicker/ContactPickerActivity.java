package com.paad.contactpicker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.actionbarsherlock.app.SherlockActivity;
import com.paad.ad2.R;

public class ContactPickerActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_contact_picker);
    }
}