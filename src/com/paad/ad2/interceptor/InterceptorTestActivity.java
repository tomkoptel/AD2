package com.paad.ad2.interceptor;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.paad.ad2.R;

public class InterceptorTestActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interceptor_main);
    }
}
