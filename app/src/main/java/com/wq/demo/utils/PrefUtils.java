package com.wq.demo.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wq.demo.bean.WeatherData;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by WQ on 2016/10/24.
 */

public class PrefUtils {

	public static void EditWeatherPref(String response, SharedPreferences pref) {
		WeatherData weatherData;
		WeatherData.WeatherBody weatherBody;
		ArrayList<WeatherData.WeatherInfo> weatherInfos;

		Gson gson = new Gson();
		weatherData = gson.fromJson(response, WeatherData.class);
		weatherBody = weatherData.showapi_res_body;

		SharedPreferences.Editor edit = pref.edit();
		edit.putString("city", weatherBody.cityInfo.c3);
		edit.putString("now_weather", weatherBody.now.weather);
		edit.putString("now_temperature", weatherBody.now.temperature + "℃");
		edit.putString("now_wind_direction", weatherBody.now.wind_direction);
		edit.putString("now_wind_power", weatherBody.now.wind_power);
		edit.putString("now_humidity", weatherBody.now.sd);
		edit.putString("now_air_quality", "空气" + weatherBody.now.aqiDetail.quality);
		edit.putString("now_air_quality_value", weatherBody.now.aqi);


		weatherInfos = new ArrayList<>();
		weatherInfos.add(weatherBody.f1);
		weatherInfos.add(weatherBody.f2);
		weatherInfos.add(weatherBody.f3);
		if (weatherBody.f4 != null) {
			weatherInfos.add(weatherBody.f4);
			weatherInfos.add(weatherBody.f5);
			weatherInfos.add(weatherBody.f6);
			weatherInfos.add(weatherBody.f7);
		}

		for (int i = 0; i < weatherInfos.size(); i++) {
			edit.putString("weatherInfo_date_" + i, weatherInfos.get(i).day.substring(6) + "日");
			edit.putString("weatherInfo_weather" + i, weatherInfos.get(i).day_weather);
			edit.putString("weatherInfo_wind_" + i, weatherInfos.get(i).day_wind_direction);
			edit.putString("weatherInfo_temperature_" + i, weatherInfos.get(i).day_air_temperature + "℃ ~ " + weatherInfos.get(i).night_air_temperature + "℃");
		}

		edit.putString("weatherInfo_date_" + 0, "今天");
		edit.putString("weatherInfo_date_" + 1, "明天");
		edit.putLong("weatherInfoUpdateTime", new Date().getTime());
		edit.commit();
	}
}
