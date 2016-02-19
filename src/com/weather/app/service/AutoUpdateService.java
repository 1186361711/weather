package com.weather.app.service;

import com.weather.app.receiver.AutoUpdateReceiver;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class AutoUpdateService extends Service {

	private IntentFilter intentFilter;
	private BroadcastReceiver receiver;
	private LocalBroadcastManager localBroadcastManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.weather.app.STOP_UPDATE");
		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				stopSelf();
				Toast.makeText(context, "ֹͣ���·���", Toast.LENGTH_LONG).show();
			}
		};
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(receiver, intentFilter);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int eHour = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + eHour;
		Intent i = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		return super.onStartCommand(intent, flag, startId);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		localBroadcastManager.unregisterReceiver(receiver);
	}
	
	/**
	 * ����������Ϣ��
	 */
	private void updateWeather(){
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		final String weatherCode = p.getString("weather_code", "");
		String address = " http://wthrcdn.etouch.cn/weather_mini?citykey=" + weatherCode;
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response, weatherCode);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
	

}
