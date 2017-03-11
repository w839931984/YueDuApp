package com.wq.demo.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wq.demo.R;
import com.wq.demo.bean.AreaById;
import com.wq.demo.bean.AreaByLevel;
import com.wq.demo.global.Global;
import com.wq.demo.utils.PrefUtils;
import com.wq.demo.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import okhttp3.Call;

public class WeatherActivity extends AppCompatActivity {

	private TextView textView_state;
	private TextView textView_city;
	private TextView textView_weather;
	private TextView textView_temperature;
	private TextView textView_wind_direction;
	private TextView textView_wind_power;
	private TextView textView_humidity;
	private TextView textView_air_quality;
	private TextView textView_air_quality_value;

	private ImageView btn_changeCity;
	private ImageView btn_back;

	private LinearLayout ll_weather;

	private ArrayList<View> mViews;

	private SharedPreferences mPref_weather;

	private final int HALF_HOUR = 1000 * 60 * 30;

	private boolean isAddView = false;
	private SharedPreferences mPref_config;

	private ArrayList<Integer>[] areaIdArray;
	private ArrayList<String>[] areaNameArray;

	private Spinner[] spinner;

	private int currentLevel;
	private String currentAreaName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		initView();

		setListener();

		initData();
	}

	private void initData() {
		mViews = new ArrayList<>();

		mPref_weather = getSharedPreferences("weather_info", MODE_PRIVATE);
		mPref_config = getSharedPreferences("config", MODE_PRIVATE);

		long weatherInfoUpdateTime = mPref_weather.getLong("weatherInfoUpdateTime", 0);
		long l = new Date().getTime() - weatherInfoUpdateTime;
		setViewData();
		if (l < HALF_HOUR && l >= 0) {
			//不需要更新天气
			textView_state.setText("当前天气");
		} else {
			//需要更新天气
			if (mPref_config.getBoolean("AutoUpdateWeatherInfo", true)) {
				textView_state.setText("正在更新天气");
				getWeatherData();
			} else {
				textView_state.setText("需要更新天气");
			}
		}


	}

	private void setListener() {
		btn_changeCity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showChooseCityDialog();

			}
		});
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	private void showChooseCityDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);

		builder.setTitle("请选择城市");
		//创建一个EditText对象设置为对话框中显示的View对象
		View view = View.inflate(this, R.layout.dialog_choose_city, null);
		spinner = new Spinner[3];
		spinner[0] = (Spinner) view.findViewById(R.id.spinner_1);
		spinner[1] = (Spinner) view.findViewById(R.id.spinner_2);
		spinner[2] = (Spinner) view.findViewById(R.id.spinner_3);

		spinner[0].setEnabled(false);
		spinner[1].setEnabled(false);
		spinner[2].setEnabled(false);

		areaIdArray = new ArrayList[3];
		areaNameArray = new ArrayList[3];

		setSpinnerListener();

		getProvinceArea();

		builder.setView(view);
		//用户选好要选的选项后，点击确定按钮
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("设置好地区");
				String city = "";
				for (int i = spinner.length - 1; i>=0; i--){
					if (spinner[i].getAdapter() != null){
						city = spinner[i].getSelectedItem().toString();
						if (!city.isEmpty()){break;}
					}
				}

				mPref_weather.edit().putString("city", city).commit();
				getWeatherData();
			}
		});
		// 取消选择
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	private void setSpinnerListener() {
		for (int i = 0; i < spinner.length - 1; i++) {
			final int finalI = i;
			spinner[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					for(int j = finalI+1; j<spinner.length;j++){
						spinner[j].setEnabled(false);
						spinner[j].setAdapter(null);
					}
					String item = spinner[0].getSelectedItem().toString();
					if ((item.equals("北京市") || item.equals("天津市") || item.equals("上海市") || item.equals("重庆市")) && finalI == 1){return;}
					getArea(areaIdArray[finalI].get(spinner[finalI].getSelectedItemPosition()),finalI + 1);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
		}
	}

	public void getArea(int id, final int index) {
		OkHttpUtils.get()
				.url(Global.buildGetAreaUrlById(id))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(WeatherActivity.this, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						System.out.println(response);
						Gson gson = new Gson();
						AreaById areaById = gson.fromJson(response, AreaById.class);
						//请求过于频繁
						if (areaById.showapi_res_code == -1009) {
							return;
						}
						if (areaById.showapi_res_body.ret_code == -1){
							return;
						}
						ArrayList<AreaById.AreaData> areaDatas = areaById.showapi_res_body.data.children;
						Collections.sort(areaDatas, new Comparator<AreaById.AreaData>() {
							@Override
							public int compare(AreaById.AreaData lhs, AreaById.AreaData rhs) {
								return lhs.id - rhs.id;
							}
						});
						ArrayList<String> areaNames = new ArrayList<>();
						ArrayList<Integer> areaIds = new ArrayList<>();
						for (int i = 0; i < areaDatas.size(); i++) {
							if (areaDatas.get(i).id != 0) {
								areaIds.add(areaDatas.get(i).id);
								areaNames.add(areaDatas.get(i).areaName);
							}
						}
						areaIdArray[index] = areaIds;
						areaNameArray[index] = areaNames;

						if(areaNames.size() > 0){
							ArrayAdapter<String> adapter = new ArrayAdapter<>(WeatherActivity.this, android.R.layout.simple_spinner_item, areaNames);
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spinner[index].setAdapter(adapter);
						}

						for (int i = 0; i<spinner.length; i++){
							if (spinner[i].getAdapter() != null){
								spinner[i].setEnabled(true);
							}
						}
					}
				});
	}

	public void getProvinceArea() {
		OkHttpUtils.get()
				.url(Global.buildGetAreaUrlByLevel(1, ""))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(WeatherActivity.this, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						System.out.println(response);
						Gson gson = new Gson();
						AreaByLevel areaByLevel = gson.fromJson(response, AreaByLevel.class);
						//请求过于频繁
						if (areaByLevel.showapi_res_code == -1009) {
							return;
						}
						ArrayList<AreaByLevel.AreaData> areaDatas = areaByLevel.showapi_res_body.data;
						Collections.sort(areaDatas, new Comparator<AreaByLevel.AreaData>() {
							@Override
							public int compare(AreaByLevel.AreaData lhs, AreaByLevel.AreaData rhs) {
								return lhs.id - rhs.id;
							}
						});
						ArrayList<String> areaNames = new ArrayList<>();
						ArrayList<Integer> areaIds = new ArrayList<>();
						for (int i = 0; i < areaDatas.size(); i++) {
							if (i == 34) {
								break;
							}
							areaIds.add(areaDatas.get(i).id);
							areaNames.add(areaDatas.get(i).areaName);
						}
						areaIdArray[0] = areaIds;
						areaNameArray[0] = areaNames;
						ArrayAdapter<String> adapter = new ArrayAdapter<>(WeatherActivity.this, android.R.layout.simple_spinner_item, areaNames);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner[0].setAdapter(adapter);
					}
				});
	}

	private void initView() {
		textView_state = (TextView) findViewById(R.id.tv_state);
		textView_city = (TextView) findViewById(R.id.tv_city);
		textView_weather = (TextView) findViewById(R.id.tv_weather);
		textView_temperature = (TextView) findViewById(R.id.tv_temperature);
		textView_wind_direction = (TextView) findViewById(R.id.tv_wind_direction);
		textView_wind_power = (TextView) findViewById(R.id.tv_wind_power);
		textView_humidity = (TextView) findViewById(R.id.tv_humidity);
		textView_air_quality = (TextView) findViewById(R.id.tv_air_quality);
		textView_air_quality_value = (TextView) findViewById(R.id.tv_air_quality_value);
		btn_changeCity = (ImageView) findViewById(R.id.btn_changCity);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		ll_weather = (LinearLayout) findViewById(R.id.ll_weather);
	}

	private void setViewData() {
		textView_state.setText("当前天气");
		textView_city.setText(mPref_weather.getString("city", "南京"));
		textView_weather.setText(mPref_weather.getString("now_weather", "晴"));
		textView_temperature.setText(mPref_weather.getString("now_temperature", "20℃"));
		textView_wind_direction.setText(mPref_weather.getString("now_wind_direction", "东南风"));
		textView_wind_power.setText(mPref_weather.getString("now_wind_power", "3级"));
		textView_humidity.setText(mPref_weather.getString("now_humidity", "30"));
		textView_air_quality.setText(mPref_weather.getString("now_air_quality", "优"));
		textView_air_quality_value.setText(mPref_weather.getString("now_air_quality_value", "10"));

		if (mViews.size() == 0) {
			for (int i = 0; i < 6; i++) {
				View view = View.inflate(this, R.layout.weather_item, null);
				TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
				TextView tv_weather = (TextView) view.findViewById(R.id.tv_weather);
				TextView tv_wind = (TextView) view.findViewById(R.id.tv_wind);
				TextView tv_temperature = (TextView) view.findViewById(R.id.tv_temperature);

				tv_date.setText(mPref_weather.getString("weatherInfo_date_" + i, ""));
				tv_weather.setText(mPref_weather.getString("weatherInfo_weather" + i, ""));
				tv_wind.setText(mPref_weather.getString("weatherInfo_wind_" + i, ""));
				tv_temperature.setText(mPref_weather.getString("weatherInfo_temperature_" + i, ""));

				mViews.add(view);
			}
		} else {
			for (int i = 0; i < 6; i++) {
				View view = mViews.get(i);

				TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
				TextView tv_weather = (TextView) view.findViewById(R.id.tv_weather);
				TextView tv_wind = (TextView) view.findViewById(R.id.tv_wind);
				TextView tv_temperature = (TextView) view.findViewById(R.id.tv_temperature);

				tv_date.setText(mPref_weather.getString("weatherInfo_date_" + i, ""));
				tv_weather.setText(mPref_weather.getString("weatherInfo_weather" + i, ""));
				tv_wind.setText(mPref_weather.getString("weatherInfo_wind_" + i, ""));
				tv_temperature.setText(mPref_weather.getString("weatherInfo_temperature_" + i, ""));
			}
		}

		if (isAddView) {
			return;
		}
		addViews(mViews);
	}

	private void addViews(ArrayList<View> views) {
		if (isAddView) {
			return;
		}
		for (int i = 0; i < views.size(); i++) {
			ll_weather.addView(views.get(i));
		}

		isAddView = true;
	}

	public void getWeatherData() {
		OkHttpUtils.get()
				.url(Global.buildWeatherDataUrlByCityName(mPref_weather.getString("city", "南京")))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(WeatherActivity.this, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						System.out.println(response);
						PrefUtils.EditWeatherPref(response, mPref_weather);

						setViewData();
					}
				});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
