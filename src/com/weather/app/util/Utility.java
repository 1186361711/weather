package com.weather.app.util;

import android.text.TextUtils;

import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;
import com.weather.app.model.WeatherDB;

public class Utility {

	/**
	 * 将省级数据解析并存入数据库
	 */
	public synchronized static boolean handlleProvinceResponse(WeatherDB weatherDB, String response){
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
	public synchronized static boolean handlleCityResponse(WeatherDB weatherDB, String response, int provinceId){
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
	public synchronized static boolean handlleCountyResponse(WeatherDB weatherDB, String response, int cityId){
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
	
}
