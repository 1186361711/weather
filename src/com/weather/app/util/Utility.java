package com.weather.app.util;

import java.util.Calendar;

import org.json.JSONArray;
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

	static Calendar calendar = Calendar.getInstance();
	static int year = calendar.get(Calendar.YEAR);
	static int month = calendar.get(Calendar.MONTH)+1;
	
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
	public static void handleWeatherResponse(Context context,String response, String weatherCode){
	
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject dataObject = jsonObject.getJSONObject("data");
			String cityName = dataObject.getString("city");
			JSONArray jsonArray = new JSONArray(dataObject.getString("forecast"));
			String[] weatherDesp = new String[5];
			String[] temp1 = new String[5];
			String[] temp2 = new String[5];
			String[] date = new String[5];
			for(int i = 0; i < 5; i++){
				JSONObject weatherObject = jsonArray.getJSONObject(i);
				weatherDesp[i] = weatherObject.getString("type");
				temp1[i] = weatherObject.getString("high").split(" ")[1];
				temp2[i] = weatherObject.getString("low").split(" ")[1];
				date[i] = weatherObject.getString("date");
			}
			//String weatherCode = weatherObject.getString("cityid");
			//String publishTime = weatherObject.getString("ptime");
			/*Log.d("Utility", "cityName "+cityName);
			Log.d("Utility", "cityid " +weatherCode);
			Log.d("Utility", "weather " +weatherDesp);
			Log.d("Utility", "temp1 " +temp1[1]);
			Log.d("Utility", "temp2 " +temp2[1]);*/
			//Log.d("Utility", "ptime " +publishTime);
			
			saveWeatherInfo(context, cityName, weatherCode, date, weatherDesp, temp1, temp2/*, publishTime*/);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 将解析后的天气信息存入到SharedPreferences文件中。
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String[] date, String[] weatherDesp, String[] temp1, String[] temp2/*,
			String publishTime*/) {
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		for(int i =0; i < 5; i++){
			editor.putString("temp1_"+i, temp1[i]);
			editor.putString("temp2_"+i, temp2[i]);
			editor.putString("weather_desp_"+i, weatherDesp[i]);
			if(i == 0){
				editor.putString("current_date_"+i, "今天");
			}else{
				String day =date[i].substring(0, 2);
				String dayEarlier = date[i-1].substring(0, 2);
				Log.d("Utility",day+"  "+dayEarlier);
				if(day.compareTo(dayEarlier) < 0){
					month++;
					if(month > 12){
						year++;
						month = 1;
					}
				}
				//Log.d("Utility",year+"年"+month+"月"+date[i]);
				editor.putString("current_date_"+i, year+"年"+month+"月"+date[i]);
			}
			//editor.putString("current_date_"+i, date[i]);
		}
		editor.commit();
		//editor.putString("publish_time", publishTime);
		//Log.d("Utility","saveWeatherInfo"+weatherCode);
	}
}
