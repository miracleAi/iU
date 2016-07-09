package com.android.biubiu.component.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView{
	public MyGridView(Context context, AttributeSet attrs) { 
		super(context, attrs); 
	} 
	public MyGridView(Context context) { 
		super(context); 
	} 
	public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){  
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, mExpandSpec);  
	}  
}
