package com.paad.ad2.samples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View{
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec){
        int measuredWidth = measureWidth(wMeasureSpec);
        int measuredHeight = measureHeight(hMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 500;

        if(specMode == MeasureSpec.AT_MOST){
            result = specSize;
        }else if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 500;

        if(specMode == MeasureSpec.AT_MOST){
            result = specSize;
        }else if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas){
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int px = width/2;
        int py = height/2;

        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);

        String displayText = "Hello world";

        float textWidth = mTextPaint.measureText(displayText);

        canvas.drawText(displayText, px-textWidth/2, py, mTextPaint);
    }
}
