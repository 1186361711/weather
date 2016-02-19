package com.weather.app.activity;

import com.weather.app.R;
import com.weather.app.service.AutoUpdateService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.style.IconMarginSpan;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class Setting extends Activity{
	
	private CheckBox updateCheckBox;
	private SharedPreferences.Editor editor;
	private SharedPreferences spref;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_layout);
		updateCheckBox = (CheckBox) findViewById(R.id.update_checkbox);
		spref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isUpdate = spref.getBoolean("is_update", true);
		if(isUpdate){
			updateCheckBox.setChecked(true);
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		editor = spref.edit();
		if(updateCheckBox.isChecked()){
			editor.putBoolean("is_update", true);
			Intent intent = new Intent(this,AutoUpdateService.class);
			startService(intent);
		}else{
			editor.putBoolean("is_update", false);
			Intent intent = new Intent("com.weather.app.STOP_UPDATE");
			LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
			localBroadcastManager.sendBroadcastSync(intent);
		}
		editor.commit();
	}

}
