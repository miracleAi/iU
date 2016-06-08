package com.android.biubiu.component.viewflipper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

/**
 * Created by yhj-pc on 15/10/28.
 */
public class ViewFlipperForListview extends ViewFlipper {
    public ViewFlipperForListview(Context context) {
        super(context);
    }

    public ViewFlipperForListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
        return super.dispatchTouchEvent(ev);
    }
}
