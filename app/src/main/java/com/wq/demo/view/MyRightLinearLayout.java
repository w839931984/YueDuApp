package com.wq.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.wq.demo.drag.DragFrameLayout;

/**
 * Created by WQ on 2016/10/11.
 */

public class MyRightLinearLayout extends LinearLayout {
	private DragFrameLayout dragLayout;

	public MyRightLinearLayout(Context context) {
		this(context, null);
	}

	public MyRightLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyRightLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setDragLayout(DragFrameLayout dragLayout){
		this.dragLayout = dragLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (dragLayout.getState() == DragFrameLayout.Status.Opened) {
			return true;
		}else{
			return super.onInterceptTouchEvent(ev);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (dragLayout.getState() == DragFrameLayout.Status.Closed) {
			return super.onTouchEvent(event);

		}else if(dragLayout.getState() == DragFrameLayout.Status.Opened){
			if (event.getAction() == MotionEvent.ACTION_UP)
				dragLayout.change();
			return true;
		}else {
			return true;
		}
	}
}
