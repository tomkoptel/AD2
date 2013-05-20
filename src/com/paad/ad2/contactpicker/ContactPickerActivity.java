package com.paad.ad2.contactpicker;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.paad.ad2.R;

public class ContactPickerActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_contact_picker);
    }
}