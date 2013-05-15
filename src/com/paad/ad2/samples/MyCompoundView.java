package com.paad.ad2.samples;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.paad.ad2.R;

public class MyCompoundView extends LinearLayout implements Button.OnClickListener{
    private TextView editText;
    private Button clearButton;

    public MyCompoundView(Context context) {
        super(context);
        init(context);
    }

    public MyCompoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        String infService = context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(R.layout.clearable_edit_text, this, true);

        editText = (TextView) findViewById(R.id.editText);
        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        editText.setText("");
    }
}
