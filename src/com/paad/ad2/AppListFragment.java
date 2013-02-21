package com.paad.ad2;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import com.paad.compass.CompassActivity;
import com.paad.contactpicker.ContactPickerTester;
import com.paad.earthquake.EarthquakeActivity;
import com.paad.surfacecamera.SurfaceActivity;
import com.paad.todolist.ToDoListActivity;

public class AppListFragment extends ListFragment {
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
