package com.paad.compass;

import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.paad.ad2.R;

public class CompassActivity extends SherlockActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compas_main);
        CompassView compassView = (CompassView) findViewById(R.id.compassView);
        compassView.setBearing(45);
    }
}