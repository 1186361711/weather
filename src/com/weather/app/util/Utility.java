package com.weather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;
import com.weather.app.model.WeatherDB;

public class Utility {

	/**
	 * 将省级数据解析并存入数据库
	 */
	public synchronized static boolean handleProvinceResponse(WeatherDB weatherDB, String response){
		if(! TextUtils.isEmpty(response)){
			String[] allProvince = response.split(",");
			if(allProvince != null && allProvince.length > 0){
				for(String p : allProvince){
					String[] s = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(s[0]);
					province.setProvinceName(s[1]);
					weatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将市级数据解析并存入数据库
	 */
	public synchronized static boolean handleCityResponse(WeatherDB weatherDB, String response, int provinceId){
		if(! TextUtils.isEmpty(response)){
			String[] allCity = response.split(",");
			if(allCity != null && allCity.length > 0){
				for(String c : allCity){
					String[] s = c.split("\\|");
					City city = new City();
					city.setCityCode(s[0]);
					city.setCityName(s[1]);
					city.setProvinceId(provinceId);
					weatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将县级数据解析并存入数据库
	 */
	public synchronized static boolean handleCountyResponse(WeatherDB weatherDB, String response, int cityId){
		if(! TextUtils.isEmpty(response)){
			String[] allCounty = response.split(",");
			if(allCounty != null && allCounty.length > 0){
				for(String c : allCounty){
					String[] s = c.split("\\|");
					County county = new County();
					county.setCountyCode(s[0]);
					county.setCountyName(s[1]);
					county.setCityId(cityId);
					weatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析服务器返回的JSON数据并将解析出的数据存储到本地。
	 */
	public static void handleWeatherResponse(Context context,String response){
	
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherObject = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherObject.getString("city");
			String weatherCode = weatherObject.getString("cityid");
			String weatherDesp = weatherObject.getString("weather");
			String temp1 = weatherObject.getString("temp1");
			String temp2 = weatherObject.getString("temp2");
			String publishTime = weatherObject.getString("ptime");
			Log.d("Utility", "cityName "+cityName);
			Log.d("Utility", "cityid " +weatherCode);
			Log.d("Utility", "weather " +weatherDesp);
			Log.d("Utility", "temp1 " +temp1);
			Log.d("Utility", "temp2 " +temp2);
			Log.d("Utility", "ptime " +publishTime);
			
			saveWeatherInfo(context, cityName, weatherCode, weatherDesp, temp1, temp2, publishTime);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 将解析后的天气信息存入到SharedPreferences文件中。
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String weatherDesp, String temp1, String temp2,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
