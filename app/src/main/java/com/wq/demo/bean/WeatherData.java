package com.wq.demo.bean;

/**
 * Created by WQ on 2016/10/21.
 */

public class WeatherData extends BaseData {
	public String desc;
	public int status;
	public WeatherBody showapi_res_body;

	public class WeatherBody{
		public String time;//预报发布时间
		public int ret_code;//
		public CityInfo cityInfo;//查询的地区基本资料
		public NowWeatherInfo now;//现在实时的天气情况
		public WeatherInfo f1;//今天的天气预报
		public WeatherInfo f2;//
		public WeatherInfo f3;//
		public WeatherInfo f4;//
		public WeatherInfo f5;//
		public WeatherInfo f6;//
		public WeatherInfo f7;//
	}

	public class NowWeatherInfo{
		public AqiInfo aqiDetail;//aqi明细数据
		public String weather_code;//
		public String wind_direction;//风向
		public String temperature_time;//获得气温的时间
		public String wind_power;//风力
		public String aqi;//空气指数，越小越好
		public String sd;//空气湿度
		public String weather_pic;//天气小图标
		public String weather;//天气
		public String temperature;//气温
	}

	public class WeatherInfo{
		public String day_weather;//白天天气
		public String night_weather;//晚上天气
		public String night_weather_code;//晚上的天气编码
		public String jiangshui;//降水概率
		public String air_press;//大气压
		public String night_wind_power;//晚上风力编号
		public String day_wind_power;//白天风力编号
		public String day_weather_code;//白天的天气编码
		public String sun_begin_end;//日出日落时间(中间用|分割)
		public String ziwaixian;//紫外线
		public String day_weather_pic;//白天天气图标
		public String weekday;//星期几
		public String night_air_temperature;//晚上天气温度(摄氏度)
		public String day_air_temperature;//白天天气温度(摄氏度)
		public String day_wind_direction;//白天风向编号
		public String day;//当前天
		public String night_weather_pic;//晚上天气图标
		public String night_wind_direction;//晚上风向编号
	}

	public class AqiInfo{
		public String co;//一氧化碳1小时平均
		public String so2;//二氧化硫1小时平均
		public String area;//地区
		public String o3;//臭氧1小时平均
		public String no2;//二氧化氮1小时平均
		public String area_code;//
		public String quality;//空气质量指数类别，有“优、良、轻度污染、中度污染、重度污染、严重污染”6类
		public String aqi;//空气质量指数，越小越好
		public String pm10;//颗粒物（粒径小于等于10μm）1小时平均
		public String pm2_5;//颗粒物（粒径小于等于2.5μm）1小时平均
		public String o3_8h;//臭氧8小时平均
		public String primary_pollutant;//首要污染物
	}

	public class CityInfo{
		public String c1;//区域id
		public String c2;//城市英文名
		public String c3;//城市中文名
		public String c4;//城市所在市英文名
		public String c5;//城市所在市中文名
		public String c6;//城市所在省英文名
		public String c7;//城市所在省中文名
		public String c8;//城市所在国家英文名
		public String c9;//城市所在国家中文名
		public String c10;//城市级别
		public String c11;//城市区号
		public String c12;//邮编
		public String longitude;//经度
		public String latitude;//纬度
		public String c15;//海拔
		public String c16;//雷达站号
		public String c17;//
	}
}
