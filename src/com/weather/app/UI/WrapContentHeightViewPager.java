package com.weather.app.UI;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class WrapContentHeightViewPager extends ViewPager {

	public WrapContentHeightViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		int height = 0;
		for(int i = 0; i < getChildCount(); i++){
			View childView = getChildAt(i);
			childView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = childView.getMeasuredHeight();
			if(height < h){
				height = h;
			}
		}
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
