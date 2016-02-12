package com.weather.app.activity;

import com.weather.app.R;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishTimeText;
	private TextView currentDateText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishTimeText = (TextView) findViewById(R.id.publish_text);
		currentDateText = (TextView) findViewById(R.id.current_date);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			publishTimeText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
		
	}

	/**
	 * ��ѯ�ؼ����Ŷ�Ӧ���������š�
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"+ countyCode +".xml";
		queryFromServer(address, "countyCode");
	}
	
	/**
	 * ��ѯ�������Ŷ�Ӧ��������Ϣ��
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"+ weatherCode +".html";
		queryFromServer(address, "weatherCode");
	}

	/**
	 * �ӷ�������ѯ�������Ż�������Ϣ
	 * @param address 
	 * @param type 
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				if("countyCode".equals(type)){
					Log.d("WeatherActivity","county"+response);
					if(!TextUtils.isEmpty(response)){
						String[] s = response.split("\\|");
						if(s != null && s.length == 2){
							String weatherCode = s[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					Log.d("WeatherActivity","weather"+response);
					Utility.handleWeatherResponse(WeatherActivity.this, response);
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
						publishTimeText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/**
	 * ��SharedPreferrences�ļ��ж���������Ϣ����ʾ�ڽ����ϡ�
	 */
	private void showWeather() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(p.getString("city_name", ""));
		publishTimeText.setText("����"+p.getString("publish_time", "")+"����");
		currentDateText.setText(p.getString("current_date", ""));
		weatherDespText.setText(p.getString("weather_desp", ""));
		temp1Text.setText(p.getString("temp1", ""));
		temp2Text.setText(p.getString("temp2", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
	
}

