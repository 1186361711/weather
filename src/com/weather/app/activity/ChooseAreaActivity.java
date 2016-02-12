package com.weather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.weather.app.R;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;
import com.weather.app.model.WeatherDB;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private TextView textView;
	private ListView listView;
	private ProgressDialog progressDialog;
	
	private Province selectedProvince;
	private City selectedCity;
	private ArrayAdapter<String> adapter;
	
	private WeatherDB weatherDB;
	
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private int currentLevel;
	
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		if(p.getBoolean("city_selected", false)){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area); 
		textView = (TextView) findViewById(R.id.title_text);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		weatherDB = WeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					quaryCity();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					quaryCounty();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		quaryProvince();
	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��в�ѯ����û���ٵ��������ϲ�ѯ
	 */
	private void quaryProvince() {
		provinceList = weatherDB.loadProvince();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			quaryFromServer(null,"province");
		}
	}
	
	/**
	 * ��ѯ��ѡʡ���е��У����ȴ����ݿ��в�ѯ����û���ٵ��������ϲ�ѯ
	 */
	private void quaryCity() {
		cityList = weatherDB.loadCity(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City c : cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			quaryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	/**
	 * ��ѯ��ѡ�����е��أ����ȴ����ݿ��в�ѯ����û���ٵ��������ϲ�ѯ
	 */
	private void quaryCounty() {
		countyList = weatherDB.loadCounty(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County c : countyList){
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			quaryFromServer(selectedCity.getCityCode(),"county");
		}
	}

	/**
	 * �ӷ������ϲ�ѯʡ���ص���Ϣ
	 * @param code ʡ���ж�Ӧ�ı���
	 * @param type ��ѯ����
	 */
	private void quaryFromServer(final String code, final String type) {
		
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		Log.d("ChooseAreaActivity","quaryFS "+address);
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvinceResponse(weatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handleCityResponse(weatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handleCountyResponse(weatherDB, response, selectedCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
							if("province".equals(type)){
								quaryProvince();
							}else if("city".equals(type)){
								quaryCity();
							}else if("county".equals(type)){
								quaryCounty();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "��ȡʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * ȡ�����ȶԻ���
	 */
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����back���������ݵ�ǰ�������жϸ÷���ʡ�����б������ֱ���˳�
	 */
	@Override
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			quaryCity();
		}else if(currentLevel == LEVEL_CITY){
			quaryProvince();
		}else{
			finish();
		}
	}
}
