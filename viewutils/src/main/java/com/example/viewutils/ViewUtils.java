package com.example.viewutils;

import android.app.Activity;
import android.view.View;

import com.example.viewutils.Annotation.ViewInject;

import java.lang.reflect.Field;

/**
 * Created by WQ on 2017/3/20.
 */

public class ViewUtils {
    public static void inject(Activity activity) {
        Class<? extends Activity> activityClass = activity.getClass();
        Field[] fields = activityClass.getDeclaredFields();
        for (Field field : fields) {
            ViewInject annotation = field.getAnnotation(ViewInject.class);
            if (annotation != null) {
                int resId = annotation.value();
                View view = activity.findViewById(resId);
                try {
                    field.setAccessible(true);
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
