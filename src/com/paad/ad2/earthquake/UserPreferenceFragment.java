package com.paad.ad2.earthquake;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.paad.ad2.R;

public class UserPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }
}