package com.weather.app.activity;

import java.util.ArrayList;

import com.weather.app.R;
import com.weather.app.UI.WeatherPagerAdapter;
import com.weather.app.UI.WrapContentHeightViewPager;
import com.weather.app.service.AutoUpdateService;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{

	//private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishTimeText;
	private TextView currentDateText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private Button switchCity;
	private Button refreshWeather;
	//private ViewPager weatherInfoPager;
	private ArrayList<View> pagerViewList;
	private WeatherPagerAdapter adapter;
	private WrapContentHeightViewPager weatherInfoPager;
	private View currentView;  //ViewPager中的当前页
	private int currentIndex;  //当前页在List中对应的角标
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoPager = (WrapContentHeightViewPager) findViewById(R.id.weather_view_pager);
		LayoutInflater inflater = getLayoutInflater();
		View View1 = inflater.inflate(R.layout.single_day_layout, null);
		View View2 = inflater.inflate(R.layout.single_day_layout, null);
		View View3 = inflater.inflate(R.layout.single_day_layout, null);
		View View4 = inflater.inflate(R.layout.single_day_layout, null);
		View View5 = inflater.inflate(R.layout.single_day_layout, null);
		
		pagerViewList = new ArrayList<View>();
		pagerViewList.add(View1);
		pagerViewList.add(View2);
		pagerViewList.add(View3);
		pagerViewList.add(View4);
		pagerViewList.add(View5);
		
		adapter = new WeatherPagerAdapter(pagerViewList);
		weatherInfoPager.setAdapter(adapter);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishTimeText = (TextView) findViewById(R.id.publish_text);
		
		currentIndex = weatherInfoPager.getCurrentItem();
		currentView = pagerViewList.get(currentIndex);
		currentDateText = (TextView) currentView.findViewById(R.id.current_date);
		temp1Text = (TextView) currentView.findViewById(R.id.temp1);
		temp2Text = (TextView) currentView.findViewById(R.id.temp2);
		weatherDespText = (TextView) currentView.findViewById(R.id.weather_desp);
		
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity = (Button) findViewById(R.id.switch_city);
		
		String countyCode = getIntent().getStringExtra("county_code");
		Log.d("WeatherActivity","from_intent"+countyCode);
		if(!TextUtils.isEmpty(countyCode)){
			publishTimeText.setVisibility(View.VISIBLE);
			publishTimeText.setText("同步中...");
			weatherInfoPager.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
		
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		weatherInfoPager.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				currentIndex = position;
				currentView = pagerViewList.get(position);
				currentDateText = (TextView) currentView.findViewById(R.id.current_date);
				temp1Text = (TextView) currentView.findViewById(R.id.temp1);
				temp2Text = (TextView) currentView.findViewById(R.id.temp2);
				weatherDespText = (TextView) currentView.findViewById(R.id.weather_desp);
				showWeather();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class); 
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishTimeText.setVisibility(View.VISIBLE);
			publishTimeText.setText("同步中...");
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = p.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
			
	}

	/**
	 * 查询县级代号对应的天气代号。
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"+ countyCode +".xml";
		queryFromServer(address, "countyCode", null);
	}
	
	/**
	 * 查询天气代号对应的天气信息。
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
//		String address = "http://www.weather.com.cn/data/cityinfo/"+ weatherCode +".html";
		String address = " http://wthrcdn.etouch.cn/weather_mini?citykey=" + weatherCode;
		queryFromServer(address, "weatherCode", weatherCode);
	}

	/**
	 * 从服务器查询天气代号或天气信息
	 * @param address 
	 * @param type 
	 */
	private void queryFromServer(final String address, final String type, final String weatherCode) {
		Log.d("WeatherAvtivity","queryFromServer"+address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				if("countyCode".equals(type)){
					Log.d("WeatherActivity","county"+response);
					if(!TextUtils.isEmpty(response)){
						String[] s = response.split("\\|");
						if(s != null && s.length == 2){
							String weatherCode1 = s[1];
							queryWeatherInfo(weatherCode1);
						}
					}
				}else if("weatherCode".equals(type)){
					//Log.d("WeatherActivity","weather"+response);
					Utility.handleWeatherResponse(WeatherActivity.this, response, weatherCode);
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						publishTimeText.setVisibility(View.VISIBLE);
						publishTimeText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/**
	 * 从SharedPreferrences文件中读出天气信息并显示在界面上。
	 */
	private void showWeather() {
		Log.d("WeatherAvtivity","currentIndex:"+currentIndex);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		adapter.notifyDataSetChanged();
		cityNameText.setText(p.getString("city_name", ""));
		publishTimeText.setVisibility(View.INVISIBLE);
		//publishTimeText.setText("今天"/*+p.getString("publish_time", "")*/+"---"+"发布");
		currentDateText.setText(p.getString("current_date_"+currentIndex, ""));
		weatherDespText.setText(p.getString("weather_desp_"+currentIndex, ""));
		temp1Text.setText(p.getString("temp1_"+currentIndex, ""));
		temp2Text.setText(p.getString("temp2_"+currentIndex, ""));
		weatherInfoPager.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		boolean autoUpdateFlag = p.getBoolean("update_weather_switch", true);
		if(autoUpdateFlag){
			Intent intent = new Intent(this, AutoUpdateService.class);
			startService(intent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.auto_update_weather:
			Intent intent = new Intent(this,Setting.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return true;
	}
}

