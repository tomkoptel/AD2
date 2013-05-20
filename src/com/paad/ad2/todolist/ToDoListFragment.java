package com.paad.ad2.todolist;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;

public class ToDoListFragment extends ListFragment implements AdapterView.OnItemLongClickListener {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        ToDoListActivity activity = (ToDoListActivity) getActivity();
        String itemId = activity.todoItems.get(position).getId();
        activity.getIntent().putExtra(ToDoListActivity.ITEM_ID, itemId);
        activity.showConfirmDialog();
        return true;
    }
}
