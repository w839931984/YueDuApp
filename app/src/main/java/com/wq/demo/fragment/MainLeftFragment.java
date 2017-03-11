package com.wq.demo.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wq.demo.R;
import com.wq.demo.activity.MainActivity;
import com.wq.demo.activity.MarkActivity;
import com.wq.demo.activity.SettingActivity;
import com.wq.demo.activity.UserSaveActivity;
import com.wq.demo.activity.VipActivity;
import com.wq.demo.activity.WeatherActivity;
import com.wq.demo.global.Global;
import com.wq.demo.utils.PrefUtils;
import com.wq.demo.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class MainLeftFragment extends BaseFragment implements View.OnClickListener {

	private ImageButton userImageButton;
	private TextView userNameTextView;
	private Button vipButton;
	private Button saveButton;
	private Button markButton;
	private Button setButton;
	private View weatherView;

	private TextView temperature;
	private TextView city;
	private TextView weather;
	private SharedPreferences mPref_weather;
	private SharedPreferences mPref_config;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_main_left, null);
		userImageButton = (ImageButton) view.findViewById(R.id.user_img);
		userNameTextView = (TextView) view.findViewById(R.id.user_name);
		vipButton = (Button) view.findViewById(R.id.btn_vip);
		saveButton = (Button) view.findViewById(R.id.btn_save);
		markButton = (Button) view.findViewById(R.id.btn_mark);
		setButton = (Button) view.findViewById(R.id.btn_setting);
		weatherView = view.findViewById(R.id.layout_weather);
		weather = (TextView) view.findViewById(R.id.weather);
		city = (TextView) view.findViewById(R.id.city);
		temperature = (TextView) view.findViewById(R.id.temperature);
		return view;
	}

	@Override
	public void initData() {
		mPref_weather = mActivity.getSharedPreferences("weather_info", MODE_PRIVATE);
		mPref_config = mActivity.getSharedPreferences("config", MODE_PRIVATE);

		if (mPref_config.getBoolean("AutoUpdateWeatherInfo", true)) {
			//更新天气
			getWeatherData();
		}else{
			upDataWeather();
		}

		setListener();
	}

	private void setListener() {
		userImageButton.setOnClickListener(this);
		vipButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		markButton.setOnClickListener(this);
		setButton.setOnClickListener(this);
		weatherView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.user_img:
//				if(用户存在){
//					//跳转到用户信息界面
//				}else{
// 					ToastUtils.showSingleShortToast(mActivity, "跳转到登陆界面");
// 				}
				break;
			case R.id.btn_vip:
				ToastUtils.showSingleShortToast(mActivity, "跳转到VIP界面");
				gotoActivity(VipActivity.class);
				break;
			case R.id.btn_save:
				ToastUtils.showSingleShortToast(mActivity, "跳转到收藏界面");
				gotoActivity(UserSaveActivity.class);
				break;
			case R.id.btn_mark:
				ToastUtils.showSingleShortToast(mActivity, "跳转到书签界面");
				gotoActivity(MarkActivity.class);
				break;
			case R.id.btn_setting:
				gotoActivity(SettingActivity.class);
				break;
			case R.id.layout_weather:
				//启动activity
				Intent intent = new Intent(mActivity, WeatherActivity.class);
				startActivityForResult(intent, Global.WEATHE_REQUEST_CODE);
				break;
		}
	}

	private void gotoActivity(Class activity) {
		//关闭侧边栏
//		((MainActivity)mActivity).getDragView().change();

		//启动activity
		Intent intent = new Intent(mActivity, activity);
		startActivity(intent);
	}

	@Override
	public void onStop() {
		super.onStop();

		//关闭侧边栏
		((MainActivity) mActivity).getDragView().closeLeft();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Global.WEATHE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				upDataWeather();
			}
		}
	}

	private void upDataWeather() {
		weather.setText(mPref_weather.getString("now_weather", "晴"));
		city.setText(mPref_weather.getString("city", "南京"));
		temperature.setText(mPref_weather.getString("now_temperature", "20℃"));
	}

	public void getWeatherData() {
		OkHttpUtils.get()
				.url(Global.buildWeatherDataUrlByCityName(mPref_weather.getString("city", "南京")))
				.build()
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(mActivity, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
//						System.out.println(response);
						PrefUtils.EditWeatherPref(response, mPref_weather);

						upDataWeather();
					}
				});
	}
}
