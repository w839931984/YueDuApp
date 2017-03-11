package com.wq.demo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.demo.R;
import com.wq.demo.bean.Book;
import com.wq.demo.bean.Progress;
import com.wq.demo.db.BookDB;
import com.wq.demo.utils.FileUtils;
import com.wq.demo.view.MyReadView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class NativeReadActivity extends AppCompatActivity {

	private TextView mTextContainerCurr;
	private TextView mTextContainerPrev;
	private TextView mTextContainerNext;
	private TextView mTextProgress;
	private MyReadView mReadView;

	private String mFilePath;
	private String mNextText_1K;
	private String mPrevText_1K;
	private String mCharSet;
	private String mCurrText_1K;

	private int mContentHeight;
	private int mLineHeight;
	private int mTextLineMaxNum;
	private int mCurrTextIndex = -1;
	private int mPrevTextIndex = -1;
	private int mNextTextIndex = -1;

	private long mCurrPageStartIndex = 0;
	private long mCurrPageEndIndex = 0;
	private long mPrevPageStartIndex = 0;
	private long mPrevPageEndIndex = 0;
	private long mNextPageStartIndex = 0;
	private long mNextPageEndIndex = 0;
	private long mFileLength = 0;

	private final int FIX_TEXTVIEW_CONTENT = 0;
	private final int ADD_TEXTVIEW_CONTENT = 1;

	private boolean isFull = false;

	private File mFile;
	private SharedPreferences mPref;
	private BookDB bookDB;
	private Progress mProgress;
	private Book mBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window window = this.getWindow();
		//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		//设置状态栏颜色
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.setStatusBarColor(getResources().getColor(R.color.readColor));
		}

		setContentView(R.layout.activity_native_read);

		findView();

		initData();

//		initTextView();

		setListener();
	}

	private void setListener() {
		mReadView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mReadView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				initTextView();
				mTextLineMaxNum = mReadView.getLineMaxNum();
				if (mFile.isFile() && mFile.exists()) {
					if (mCurrPageStartIndex >= mFileLength) {
						mCurrPageStartIndex = mFileLength - 2;
					}
					readCurrPage();
					readNextPage();
					readPrevPage();
					mReadView.setPageStatus(mCurrPageStartIndex, mCurrPageEndIndex, mFileLength);
				} else {
					Toast.makeText(NativeReadActivity.this, "文件已被删除!", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});

		mReadView.setOnPageChangedListener(new MyReadView.OnPageChangedListener() {
			@Override
			public void onArriveNextPage(TextView prev, TextView curr, TextView next) {
				readNextPage();
			}

			@Override
			public void onArrivePrevPage(TextView prev, TextView curr, TextView next) {
				readPrevPage();
			}

			@Override
			public void onPageChanged() {
				mReadView.setPageStatus(mCurrPageStartIndex, mCurrPageEndIndex, mFileLength);
			}
		});
	}

	private void readCurrPage() {
		initTextView();
		mTextContainerCurr.setText("");
		isFull = false;
		mCurrPageEndIndex = mCurrPageStartIndex;
		if (mCurrPageEndIndex < mFileLength) {
			readNovelForCurr(mTextContainerCurr);
		}
		try {
			mCurrPageEndIndex = mCurrPageStartIndex + mTextContainerCurr.getText().toString().getBytes(mCharSet).length;
			mPrevPageStartIndex = mCurrPageStartIndex - mTextContainerPrev.getText().toString().getBytes(mCharSet).length;
			mPrevPageEndIndex = mCurrPageStartIndex;
			mNextPageStartIndex = mCurrPageEndIndex;
			mNextPageEndIndex = mCurrPageEndIndex + mTextContainerNext.getText().toString().getBytes(mCharSet).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		saveProgress();
	}

	private void readPrevPage() {
		initTextView();
		mTextContainerPrev.setText("");
		isFull = false;
		mNextTextIndex = -1;
		mPrevPageEndIndex = mPrevPageStartIndex;
		if (mPrevPageEndIndex > 0) {
			readNovelForPrev(mTextContainerPrev);
		}
		try {
			mPrevPageStartIndex = mPrevPageEndIndex - mTextContainerPrev.getText().toString().getBytes(mCharSet).length;
			mCurrPageStartIndex = mPrevPageEndIndex;
			mCurrPageEndIndex = mPrevPageEndIndex + mTextContainerCurr.getText().toString().getBytes(mCharSet).length;
			mNextPageStartIndex = mCurrPageEndIndex;
			mNextPageEndIndex = mCurrPageEndIndex + mTextContainerNext.getText().toString().getBytes(mCharSet).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		saveProgress();
	}

	private void readNextPage() {
		initTextView();
		mTextContainerNext.setText("");
		isFull = false;
		mPrevTextIndex = -1;
		mNextPageStartIndex = mNextPageEndIndex;
		if (mNextPageEndIndex < mFileLength) {
			readNovelForNext(mTextContainerNext);
		}
		try {
			mNextPageEndIndex = mNextPageStartIndex + mTextContainerNext.getText().toString().getBytes(mCharSet).length;
			mCurrPageStartIndex = mNextPageStartIndex - mTextContainerCurr.getText().toString().getBytes(mCharSet).length;
			mCurrPageEndIndex = mNextPageStartIndex;
			mPrevPageStartIndex = mCurrPageStartIndex - mTextContainerPrev.getText().toString().getBytes(mCharSet).length;
			mPrevPageEndIndex = mCurrPageStartIndex;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		saveProgress();
//		System.out.println("--------------------------------------");
//		System.out.println("mPrevPageStartIndex:" + mPrevPageStartIndex);
//		System.out.println("mPrevPageEndIndex:" + mPrevPageEndIndex);
//		System.out.println("mCurrPageStartIndex:" + mCurrPageStartIndex);
//		System.out.println("mCurrPageEndIndex:" + mCurrPageEndIndex);
//		System.out.println("mNextPageStartIndex:" + mNextPageStartIndex);
//		System.out.println("mNextPageEndIndex:" + mNextPageEndIndex);
//		System.out.println("--------------------------------------");
	}

	private void initTextView() {
		mTextContainerCurr = mReadView.getTextContainerCurr();
		mTextContainerPrev = mReadView.getTextContainerPrev();
		mTextContainerNext = mReadView.getTextContainerNext();
	}

	private void initData() {
		Intent intent = getIntent();
		mFilePath = intent.getStringExtra("path");
		mFile = new File(mFilePath);
		bookDB = BookDB.getInstance(this);
		mBook = bookDB.loadBook(mFilePath);
		mProgress = new Progress();
		mProgress.setBookId(mBook.getId());
		loadProgress();
		if (mFile.isFile() && mFile.exists()) {
			mFileLength = mFile.length();
			System.out.println("mFileLength:" + mFileLength);
			try {
				mCharSet = FileUtils.getCharset(mFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//更新进度百分百
		updateTextProgress();
	}

	private void loadProgress() {
		mCurrPageStartIndex = bookDB.loadProgress(mBook.getId());
	}

	private void saveProgress() {
//		SharedPreferences.Editor edit = mPref.edit();
//		edit.putLong("mCurrPageStartIndex", mCurrPageStartIndex);
//		edit.commit();
		//更新进度百分百
		updateTextProgress();
		mProgress.setCurrentIndex(mCurrPageStartIndex);
		bookDB.saveProgress(mProgress);
	}

	private void readNovelForPrev(TextView textView) {
		//判断mText_1K有没有读取完，如果读取完则往文件之后继续读取1k的内容
		if (mPrevTextIndex < 0) {
			mPrevText_1K = FileUtils.getStringFromFile(mFile, mPrevPageStartIndex, false);
			mPrevTextIndex = 0;
		}
		int len = mPrevText_1K.length();
		//遍历mText_1K中的文字
		for (int i = mPrevTextIndex; i < len; i += 10) {
			//向textView中添加文字
			if (i + 10 < len) {
				mPrevTextIndex = i + 10;
			} else {
				mPrevTextIndex = len;
			}
			String s = mPrevText_1K.substring(len - mPrevTextIndex, len - i);
			textView.setText(s + textView.getText());
			try {
				mPrevPageStartIndex = mPrevPageEndIndex - textView.getText().toString().getBytes(mCharSet).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			//判断textView的行数是否大于允许的最大行数
			while (textView.getLineCount() > mTextLineMaxNum) {
				CharSequence text = textView.getText();
				textView.setText(text.subSequence(1, textView.length()));
				mPrevTextIndex--;
				//记录读取到的位置
				if (textView.getLineCount() <= mTextLineMaxNum) {
					isFull = true;
					return;
				}
			}
		}
		//遍历完成用于判断是否读取结束
		mPrevTextIndex = -1;

		//判断textView是否填充完成，没有填充完成则继续读取
		if (textView.getLineCount() <= mTextLineMaxNum && !isFull && mPrevPageStartIndex > 0) {
			readNovelForPrev(textView);
		}
	}

	private void readNovelForCurr(TextView textView) {
		if (mCurrTextIndex < 0) {
			mCurrText_1K = FileUtils.getStringFromFile(mFile, mCurrPageEndIndex, true);
			mCurrTextIndex = 0;
		}
		int len = mCurrText_1K.length();
		//遍历mText_1K中的文字
		for (int i = mCurrTextIndex; i < len; i += 10) {
			if (i + 10 < len) {
				mCurrTextIndex = i + 10;
			} else {
				mCurrTextIndex = len;
			}
			textView.setText(textView.getText() + mCurrText_1K.substring(i, mCurrTextIndex));
			try {
				mCurrPageEndIndex = mCurrPageStartIndex + textView.getText().toString().getBytes(mCharSet).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//判断textView的行数是否大于允许的最大行数
			while (textView.getLineCount() > mTextLineMaxNum) {
				CharSequence text = textView.getText();
				int textLength = text.length() - 1;
				textView.setText(text.subSequence(0, textLength));
				mCurrTextIndex--;
				//记录读取到的位置
				if (textView.getLineCount() <= mTextLineMaxNum) {
					isFull = true;
					return;
				}
			}
		}
		//遍历完成用于判断是否读取结束
		mCurrTextIndex = -1;

		//判断textView是否填充完成，没有填充完成则继续读取
		if (textView.getLineCount() <= mTextLineMaxNum && !isFull && mCurrPageEndIndex < mFileLength) {
			readNovelForCurr(textView);
		}
	}

	private void readNovelForNext(TextView textView) {
		//判断mText_1K有没有读取完，如果读取完则往文件之后继续读取1k的内容
		if (mNextTextIndex < 0) {
			mNextText_1K = FileUtils.getStringFromFile(mFile, mNextPageEndIndex, true);
			mNextTextIndex = 0;
		}
		int len = mNextText_1K.length();
		//遍历mText_1K中的文字
		for (int i = mNextTextIndex; i < len; i += 10) {
			//向textView中添加文字
			if (i + 10 < len) {
				mNextTextIndex = i + 10;
			} else {
				mNextTextIndex = len;
			}
			textView.append(mNextText_1K, i, mNextTextIndex);
			try {
				mNextPageEndIndex = mNextPageStartIndex + textView.getText().toString().getBytes(mCharSet).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//判断textView的行数是否大于允许的最大行数
			while (textView.getLineCount() > mTextLineMaxNum) {
				CharSequence text = textView.getText();
				int textLength = text.length() - 1;
				textView.setText(text.subSequence(0, textLength));
				mNextTextIndex--;
				//记录读取到的位置
				if (textView.getLineCount() <= mTextLineMaxNum) {
					isFull = true;
					return;
				}
			}
		}

		//遍历完成用于判断是否读取结束
		mNextTextIndex = -1;

		//判断textView是否填充完成，没有填充完成则继续读取
		System.out.println("mNextPageEndIndex:" + mNextPageEndIndex);
		if (textView.getLineCount() <= mTextLineMaxNum && !isFull && mNextPageEndIndex < mFileLength) {
			readNovelForNext(textView);
		}
	}

	private void findView() {
		mReadView = (MyReadView) findViewById(R.id.mReadView);
		mTextProgress = (TextView) findViewById(R.id.tv_progress);
	}

	private void updateTextProgress(){
		double progress = (double) mCurrPageStartIndex / mFileLength;
		String format = new DecimalFormat("#.##").format(progress);
		mTextProgress.setText(format +"%");
	}
}
