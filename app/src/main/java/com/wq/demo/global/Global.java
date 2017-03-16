package com.wq.demo.global;

import java.util.Random;

/**
 * Created by WQ on 2016/10/12.
 */

public class Global {
	public static final int WEATHE_REQUEST_CODE = 10001;

	public static String APP_ID = "25501";
	public static String APP_SIGN = "6bba6dcc31f14141b4aadda9ec78eda2";
	public static String URL_NOVEL_KIND = "http://route.showapi.com/211-3?"
			+ "showapi_appid=" + APP_ID
			+ "&showapi_sign=" + APP_SIGN;

	public static String buildNovelListUrl(String type, String keyword, String page) {
		return "http://route.showapi.com/211-2?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&typeId=" + type
				+ "&keyword=" + keyword
				+ "&page=" + page;
	}

	public static String buildWeatherDataUrlByCityName(String city) {
		return "http://route.showapi.com/9-2?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&area=" + city
				+ "&needMoreDay=1&needIndex=0&needHourData=0&need3HourForcast=0&needAlarm=0";
	}

	public static String buildWeatherDataUrlById(int id) {
		return "http://route.showapi.com/9-2?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&areaid=" + id
				+ "&needMoreDay=1&needIndex=0&needHourData=0&need3HourForcast=0&needAlarm=0";
	}

	public static String buildGetAreaUrlByLevel(int level, String areaName) {
		return "http://route.showapi.com/101-39?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&level=" + level
				+ "&areaName=" + areaName;
	}

	public static String buildGetAreaUrlById(int id) {
		return "http://route.showapi.com/101-113?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&id=" + id;
	}

	public static String buildGetNovelChapterUrlByBookId(int id) {
		return "http://route.showapi.com/211-1?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&bookId=" + id;
	}

	public static String buildGetNovelChapterContentUrlById(int bookId, int cid) {
		return "http://route.showapi.com/211-4?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&bookId=" + bookId
				+ "&cid=" + cid;
	}

	public static String buildGetJokeDataUrl(int type, int page) {
		return "http://route.showapi.com/341-" + type + "?"
				+ "showapi_appid=" + APP_ID
				+ "&showapi_sign=" + APP_SIGN
				+ "&maxResult=" + 5
				+ "&page=" + page;
	}
}
