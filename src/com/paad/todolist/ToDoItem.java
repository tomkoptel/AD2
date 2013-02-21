package com.paad.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
    String task;
    Date created;
    String id;

    public String getTask() {
        return task;
    }

    public Date getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    public ToDoItem(String _id, String _task) {
        this(_id, _task, new Date(java.lang.System.currentTimeMillis()));
    }

    public ToDoItem(String _id, String _task, Date _created) {
        id = _id;
        task = _task;
        created = _created;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateString = sdf.format(created);
        return "(" + dateString + ") " + task;
    }
}