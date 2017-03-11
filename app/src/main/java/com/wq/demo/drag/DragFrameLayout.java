package com.wq.demo.drag;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.wq.demo.R;
import com.wq.demo.view.MyLeftRelativeLayout;
import com.wq.demo.view.MyRightLinearLayout;

/**
 * Created by WQ on 2016/10/10.
 */

public class DragFrameLayout extends FrameLayout {

	private ViewDragHelper mDragHelper;
	private MyLeftRelativeLayout mLeftGroup;
	private MyRightLinearLayout mRightGroup;

	private int mWindowWidth;
	private int mWindowHeight;
	private int mRange;

	public boolean isDragenable() {
		return dragenable;
	}

	public void setDragenable(boolean dragenable) {
		this.dragenable = dragenable;
	}

	private boolean dragenable = true;

	private Status mState = Status.Closed;

	public enum Status {
		Closed, Closing, Opened, Opening
	}

	public DragFrameLayout(Context context) {
		this(context, null);
	}

	public DragFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
			@Override
			public boolean tryCaptureView(View child, int pointerId) {
				if(mState == Status.Opened || isDragenable()) {
					return child == mRightGroup;
				}
				return false;
			}

			@Override
			public int getViewHorizontalDragRange(View child) {
				return mRange;
			}

			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				if (left <= 0) {
					return 0;
				} else if (left >= mRange) {
					return mRange;
				}
				return left;
			}

			@Override
			public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
				startDragAnimation(left);

				invalidate();
			}

			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				if (xvel == 0 && mRightGroup.getLeft() > mRange / 6 || xvel > 0) {
					openLeft();
				} else {
					closeLeft();
				}
			}
		});
	}

	private void startDragAnimation(int left) {
		float percent = left * 1.0f / mRange;

		View view = mLeftGroup.findViewById(R.id.view_background);
		view.setAlpha(percent * 0.5f);
		View fragment = mLeftGroup.findViewById(R.id.main_left_fragment);
		fragment.setAlpha(percent);
		fragment.setScaleX(0.7f + 0.3f * percent);
		fragment.setScaleY(0.7f + 0.3f * percent);
		mLeftGroup.setTranslationX(-0.5f * mWindowWidth * (1 - percent));
	}

	public void closeLeft() {
		if (mDragHelper.smoothSlideViewTo(mRightGroup, 0, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		mState = Status.Closing;
	}

	public void openLeft() {
		if (mDragHelper.smoothSlideViewTo(mRightGroup, mRange, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		mState = Status.Opening;
	}

	public void change() {
		if (mState == Status.Opened || mState == Status.Opening) {
			closeLeft();
		} else {
			openLeft();
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		} else {
			if (mRightGroup.getLeft() == 0) {
				mState = Status.Closed;
			} else if (mRightGroup.getLeft() == mRange) {
				mState = Status.Opened;
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_MOVE:
				return true;
			case MotionEvent.ACTION_UP:
				return true;
		}
		return true;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mLeftGroup = (MyLeftRelativeLayout) getChildAt(0);
		mRightGroup = (MyRightLinearLayout) getChildAt(1);
//
		mLeftGroup.setDragLayout(this);
		mRightGroup.setDragLayout(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWindowWidth = w;
		mWindowHeight = h;
		mRange = (int) (mWindowWidth * 0.6);
	}

	public Status getState() {
		return mState;
	}
}
