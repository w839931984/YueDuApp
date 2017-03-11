package com.wq.demo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by WQ on 2016/8/26.
 */
public class ToastUtils {
	private static Toast toast_short = null;
	private static Toast toast_long = null;

	public static void showNormalShortToast(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showNormalLongToast(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void showSingleShortToast(Context context, String text){
		if (toast_short == null){
			toast_short = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		toast_short.setText(text);
		toast_short.show();
	}

	public static void showSingleLongToast(Context context, String text){
		if (toast_long == null){
			toast_long = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		toast_long.setText(text);
		toast_long.show();
	}
}
