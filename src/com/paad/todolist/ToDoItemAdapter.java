package com.paad.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.paad.ad2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {
    private final int resource;

    public ToDoItemAdapter(Context context, int resource, List<ToDoItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout todoView;

        ToDoItem item = getItem(position);
        String taskString = item.getTask();
        Date createdDate = item.getCreated();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateString = sdf.format(createdDate);

        if (convertView == null) {
            todoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, todoView, true);
        } else {
            todoView = (LinearLayout) convertView;
        }

        TextView dateView = (TextView) todoView.findViewById(R.id.rowDate);
        TextView taskView = (TextView) todoView.findViewById(R.id.row);
        dateView.setText(dateString);
        taskView.setText(taskString);
        return todoView;
    }
}
