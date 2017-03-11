package com.wq.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.wq.demo.drag.DragFrameLayout;

/**
 * Created by WQ on 2016/10/11.
 */

public class MyLeftRelativeLayout extends RelativeLayout {
	private DragFrameLayout dragLayout;

	public MyLeftRelativeLayout(Context context) {
		this(context, null);
	}

	public MyLeftRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyLeftRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setDragLayout(DragFrameLayout dragLayout){
		this.dragLayout = dragLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (dragLayout.getState() == DragFrameLayout.Status.Closed) {
			return true;
		}else{
			return super.onInterceptTouchEvent(ev);
		}
	}
}
