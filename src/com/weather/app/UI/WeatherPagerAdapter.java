package com.weather.app.UI;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class WeatherPagerAdapter extends PagerAdapter {

	private List<View> viewList;
	
	public WeatherPagerAdapter(List<View> list){
		this.viewList = list;
	}
	
	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position){
		container.addView(viewList.get(position),0);
		return viewList.get(position);
	}
	
	@Override
	public void destroyItem(View container, int position, Object object){
		((ViewPager)container).removeView(viewList.get(position));
	}

}
