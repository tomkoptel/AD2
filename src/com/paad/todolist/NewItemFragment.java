package com.paad.todolist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.paad.ad2.R;

public class NewItemFragment extends Fragment implements View.OnKeyListener {
    private EditText myEditText;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance) {
        View view = layoutInflater.inflate(R.layout.new_item_fragment, container, false);
        myEditText = (EditText) view.findViewById(R.id.myEditText);
        myEditText.setOnKeyListener(this);

        return view;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
            if((keyCode == KeyEvent.KEYCODE_ENTER) || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                String newItem = myEditText.getText().toString();
                onNewItemAddedListener.onNewItemAdded(newItem);
                myEditText.setText("");
                return true;
            }
        return false;
    }

    public interface OnNewItemAddedListener {
        public void onNewItemAdded(String newItem);
    }

    private OnNewItemAddedListener onNewItemAddedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onNewItemAddedListener = (OnNewItemAddedListener) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString() + " must implement OnNewItemAddedListener");
        }
    }

}
