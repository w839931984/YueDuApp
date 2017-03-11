package com.wq.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.wq.demo.R;


/**
 * Created by WQ on 2016/9/20.
 */
public class Utils {
	public static int getStatusBarHeight(Context context){
		/**
		 * 获取状态栏高度
		 * */
		int statusBarHeight = 0;
		//获取status_bar_height资源的ID
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	public static int getWindowWidth(Context context){
		return 0;
	}

	public static void setStatusBar(Activity activity){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			//android版本5.0以上
			activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.mainColor));
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			//android版本4.4以上5.0以下
			int statusBarHeight = Utils.getStatusBarHeight(activity);
			Log.i("WQ","状态栏高度："+statusBarHeight);
		}
	}
}
