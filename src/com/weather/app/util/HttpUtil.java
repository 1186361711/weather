package com.weather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {

	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){
			@Override 
			public void run(){
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					Log.d("HttpUtil",address);
					connection = (HttpURLConnection) url.openConnection();
					Log.d("HttpUtil","success");
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					/*connection.setDoInput(true);
					connection.setDoOutput(true);*/
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line;
					StringBuilder response = new StringBuilder();
					while((line = reader.readLine())!=null){
						response.append(line);
					}
					Log.d("HttpUtil","**"+response.toString()+" **");
					if(listener != null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener != null){
						listener.onError(e);// TODO: handle exception
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
