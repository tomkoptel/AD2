package com.paad.earthquake;

import android.preference.PreferenceActivity;
import com.paad.ad2.R;

import java.util.List;

public class FragmentPreferences extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
}