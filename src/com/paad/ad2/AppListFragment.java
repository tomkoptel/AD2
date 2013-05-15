package com.paad.ad2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.paad.ad2.compass.CompassActivity;
import com.paad.ad2.contactpicker.ContactPickerTester;
import com.paad.ad2.earthquake.EarthquakeActivity;
import com.paad.ad2.surfacecamera.SurfaceActivity;
import com.paad.ad2.todolist.ToDoListActivity;

public class AppListFragment extends ListFragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);

        String[] apps = getResources().getStringArray(R.array.apps);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, apps);
        setListAdapter(aa);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Class activityToLaunch = null;
        switch (position){
            case (0): activityToLaunch = ToDoListActivity.class; break;
            case (1): activityToLaunch = CompassActivity.class; break;
            case (2): activityToLaunch = ContactPickerTester.class; break;
            case (3): activityToLaunch = EarthquakeActivity.class; break;
            case (4): activityToLaunch = SurfaceActivity.class; break;
        }
        Intent intent = new Intent(getActivity(), activityToLaunch);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
