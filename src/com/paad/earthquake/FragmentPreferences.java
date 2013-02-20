package com.paad.earthquake;

import android.preference.PreferenceActivity;
import com.paad.ad2.R;

import java.util.List;

public class FragmentPreferences extends PreferenceActivity {
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
    public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
}