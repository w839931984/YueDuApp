package com.wq.demo.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * TODO: document your custom view class.
 */
public class MyReadView extends RelativeLayout {

	private TextView mTextContainerTemp;
	private TextView mTextContainerPrev;
	private TextView mTextContainerCurr;
	private TextView mTextContainerNext;
	private ViewDragHelper mDragHelper;

	private int mTextContainerHeight;
	private int mTextContainerWidth;
	private float mLineHeight;
	private int mLineMaxNum;
	private int mTextSize;

	private Status mStatus = Status.Normal;
	private PageStatus mPageStatus = PageStatus.FirstPage;

	private OnPageChangedListener mPageChangedListener;

	private enum Status {
		Normal, Moving
	}

	public enum PageStatus {
		FirstPage, LastPage, MiddlePage
	}

	public interface OnPageChangedListener {
		void onArriveNextPage(TextView prev, TextView curr, TextView next);

		void onArrivePrevPage(TextView prev, TextView curr, TextView next);

		void onPageChanged();
	}


	public MyReadView(Context context) {
		this(context, null);
	}

	public MyReadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyReadView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

//		mTextSize = DensityUtil.sp2px(context, 18.0f);

		mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
			@Override
			public boolean tryCaptureView(View child, int pointerId) {
				if (mStatus == Status.Moving) {
					return false;
				}

				return child == mTextContainerCurr;
			}

			@Override
			public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
				mTextContainerPrev.offsetLeftAndRight(dx);
				mTextContainerNext.offsetLeftAndRight(dx);

				updateStatus();

				invalidate();
			}

			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				if (xvel == 0 && mTextContainerCurr.getLeft() > mTextContainerWidth / 6 || xvel > 0) {
					goToPrevPage();
				} else if (xvel == 0 && mTextContainerCurr.getLeft() < -mTextContainerWidth / 6 || xvel < 0) {
					goToNextPage();
				} else {
					textContainerMoveAnim();
				}
			}

			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				return left;
			}
		});
	}

	private void updateStatus() {
		int left = mTextContainerCurr.getLeft();
		if (left == 0) {
			mStatus = Status.Normal;
		} else {
			mStatus = Status.Moving;
		}
	}

	private void prevTransformContainer() {
		mTextContainerTemp = mTextContainerCurr;
		mTextContainerCurr = mTextContainerPrev;
		mTextContainerPrev = mTextContainerNext;
		mTextContainerNext = mTextContainerTemp;
	}

	private void nextTransformContainer() {
		mTextContainerTemp = mTextContainerCurr;
		mTextContainerCurr = mTextContainerNext;
		mTextContainerNext = mTextContainerPrev;
		mTextContainerPrev = mTextContainerTemp;
	}

	private void goToNextPage() {
		if (mPageStatus != PageStatus.LastPage) {
			mTextContainerPrev.layout(mTextContainerNext.getRight(), 0, mTextContainerNext.getRight() + mTextContainerWidth, mTextContainerHeight);

			nextTransformContainer();

			if (mPageChangedListener != null) {
				mPageChangedListener.onArriveNextPage(mTextContainerPrev, mTextContainerCurr, mTextContainerNext);
			}
		}
		textContainerMoveAnim();
	}

	private void goToPrevPage() {
		if (mPageStatus != PageStatus.FirstPage) {
			mTextContainerNext.layout(mTextContainerPrev.getLeft() - mTextContainerWidth, 0, mTextContainerPrev.getLeft(), mTextContainerHeight);

			prevTransformContainer();

			if (mPageChangedListener != null) {
				mPageChangedListener.onArrivePrevPage(mTextContainerPrev, mTextContainerCurr, mTextContainerNext);
			}
		}
		textContainerMoveAnim();
	}

	private void textContainerMoveAnim() {
		if (mDragHelper.smoothSlideViewTo(mTextContainerCurr, 0, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		if (mPageChangedListener != null) {
			mPageChangedListener.onPageChanged();
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean onInterceptHoverEvent(MotionEvent event) {
		return mDragHelper.shouldInterceptTouchEvent(event);
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
				if (mStatus == Status.Moving) {
					return false;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if (mStatus == Status.Normal) {
					if (event.getX() > mTextContainerWidth / 2) {
						goToNextPage();
					} else {
						goToPrevPage();
					}
				}
				break;
		}

		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mTextContainerWidth = w;
		mTextContainerHeight = h;

		mLineHeight = mTextContainerCurr.getLineHeight();
		mLineMaxNum = (int) (h / mLineHeight);
	}

	private void setTextContainerSize(int textSize) {
		mTextSize = textSize;
		mTextContainerPrev.setTextSize(mTextSize);
		mTextContainerCurr.setTextSize(mTextSize);
		mTextContainerNext.setTextSize(mTextSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		initTextContainerLocation();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mTextContainerPrev = (TextView) getChildAt(0);
		mTextContainerCurr = (TextView) getChildAt(1);
		mTextContainerNext = (TextView) getChildAt(2);

//		setTextContainerSize(mTextSize);
	}

	private void initTextContainerLocation() {
		mTextContainerPrev.layout(-mTextContainerWidth, 0, 0, mTextContainerHeight);
		mTextContainerCurr.layout(0, 0, mTextContainerWidth, mTextContainerHeight);
		mTextContainerNext.layout(mTextContainerWidth, 0, 2 * mTextContainerWidth, mTextContainerHeight);
	}

	public TextView getTextContainerNext() {
		return mTextContainerNext;
	}

	public TextView getTextContainerPrev() {
		return mTextContainerPrev;
	}

	public TextView getTextContainerCurr() {
		return mTextContainerCurr;
	}

	public int getLineMaxNum() {
		return mLineMaxNum;
	}

	public void setPageStatus(long pageCurrStartIndex, long pageCurrEndIndex, long fileLength) {
		if (pageCurrStartIndex <= 0) {
			mPageStatus = PageStatus.FirstPage;
			System.out.println("mPageStatus:FirstPage");
		} else if (pageCurrEndIndex >= fileLength) {
			mPageStatus = PageStatus.LastPage;
			System.out.println("mPageStatus:LastPage");
		} else {
			mPageStatus = PageStatus.MiddlePage;
			System.out.println("mPageStatus:MiddlePage");
		}
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	public OnPageChangedListener getOnPageChangedListener() {
		return mPageChangedListener;
	}

	public void setOnPageChangedListener(OnPageChangedListener mPageChangedListener) {
		this.mPageChangedListener = mPageChangedListener;
	}
}
