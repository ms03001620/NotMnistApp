package com.example.mark.loadtensorflowmodeltest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.mark.loadtensorflowmodeltest.Classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 2017/7/20.
 */

public class InfoImageView extends android.support.v7.widget.AppCompatImageView{
    private List<Classifier.Recognition> mResults;
    private Paint mPaint;

    public InfoImageView(Context context) {
        this(context, null);
    }

    public InfoImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mResults = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setTextSize(30);
    }


    public void setResult(List<Classifier.Recognition> results){
        mResults.clear();
        mResults.addAll(results);
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mResults.size(); i++) {
            Classifier.Recognition recognition = mResults.get(i);

            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.STROKE);

            canvas.drawRect(recognition.getLocation(), mPaint);
            canvas.drawText(recognition.getTitle(), recognition.getLocation().left, recognition.getLocation().top, mPaint);
        }

    }
}
