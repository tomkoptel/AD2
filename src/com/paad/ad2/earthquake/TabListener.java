package com.paad.ad2.earthquake;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {

    public Fragment fragment;
    private Activity activity;
    private int fragmentContainer;
    private Class<T> fragmentClass;

    public TabListener(Activity activity, int fragmentContainer, Class<T> fragmentClass) {
        this.activity = activity;
        this.fragmentContainer = fragmentContainer;
        this.fragmentClass = fragmentClass;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if(fragment == null) {
            String fragmentName = fragmentClass.getName();
            fragment = Fragment.instantiate(activity, fragmentName);
            fragmentTransaction.add(fragmentContainer, fragment, fragmentName);
        } else {
            fragmentTransaction.attach(fragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if(fragment != null) {
            fragmentTransaction.detach(fragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if(fragment != null) {
            fragmentTransaction.attach(fragment);
        }
    }
}
