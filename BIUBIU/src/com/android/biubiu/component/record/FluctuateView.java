package com.android.biubiu.component.record;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import java.util.Random;

import cc.imeetu.iu.R;

/**
 * AudioView[v 1.0.0]
 * classes:com.lly.rxjavaretrofitdemo.view.AudioView
 *
 * @author lileiyi
 * @date 2016/3/28
 * @time 9:46
 * @description
 */
public class FluctuateView extends View {
    /**
     * 刷新频率
     */
    private static final int LONGTIME = 500;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 线数量
     */
    private int mLineCount = 8;
    /**
     * 颜色
     */
    private int mLineColor;
    /**
     * 线的宽度
     */
    private int mLineWidth;
    /**
     * 间距
     */
    private int mLineSpacing;
    /**
     * 是否停止标示
     */
    private boolean isStop = true;

    //低
    private static final int LOW = 0;
    //一般
    private static final int GENERAL = 1;
    //中等
    private static final int MEDIUM = 2;
    //高
    private static final int HIGH = 3;

    /**
     * 抖动的频率
     */
    private int mShake = LOW;

    public void setmShake(int mShake) {
        this.mShake = mShake;
    }

    public FluctuateView(Context context) {
        this(context, null);
    }

    public FluctuateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FluctuateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FluctuateView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.FluctuateView_line_color:
                    mLineColor = array.getColor(attr, Color.GREEN);//默认绿色
                    break;
                case R.styleable.FluctuateView_line_count:
                    mLineCount = array.getInteger(attr, 0);
                    break;
                case R.styleable.FluctuateView_line_spacing:
                    mLineSpacing = array.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.FluctuateView_line_width:
                    mLineWidth = array.getDimensionPixelSize(attr, 0);
            }
        }
        array.recycle();
        mPaint = new Paint();
        mPaint.setColor(mLineColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLineCount > 0) {
            for (int i = 0; i < mLineCount; i++) {
                float left = (i * mLineSpacing) + (i * mLineWidth);
                int random = new Random().nextInt(getHeight());
                float top = random;
                float right = left + mLineWidth;
                float bottom = getHeight();
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        //不允许直接写死宽度
        if (widthMode == MeasureSpec.EXACTLY) {
            width = (mLineCount * mLineWidth) + ((mLineCount - 1) * mLineSpacing);
        } else {
            width = (mLineCount * mLineWidth) + ((mLineCount - 1) * mLineSpacing);
        }
        setMeasuredDimension(width, heightSize);
    }

    /**
     * 开始
     */
    public void start() {
        isStop = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isStop) {
                    try {
                        sleep(LONGTIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }
            }
        }.start();
    }

    /**
     * 停止
     */
    public void stop() {
        isStop = false;
    }


}
