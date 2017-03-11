package com.wq.demo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

import com.wq.demo.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

	private SwitchCompat switch_weather;
	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolBar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle("设置");
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_previous_item);
		}

		initView();

		initData();

		setListener();
	}

	private void setListener() {
//		switch_weather.setOnClickListener(this);
		switch_weather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPref.edit().putBoolean("AutoUpdateWeatherInfo", isChecked).commit();
			}
		});
	}

	private void initData() {
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		switch_weather.setChecked(mPref.getBoolean("AutoUpdateWeatherInfo", true));
	}

	private void initView() {
		switch_weather = (SwitchCompat) findViewById(R.id.switch_weather);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){

		}
	}
}
