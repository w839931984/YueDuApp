package com.wq.demo.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wq.demo.R;
import com.wq.demo.bean.ChapterContentData;
import com.wq.demo.bean.NovelChapterData;
import com.wq.demo.global.Global;
import com.wq.demo.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;

public class OnlineReadActivity extends AppCompatActivity {

	private TextView tv_content;
	private ListView lv_chapter;
	private ProgressBar progressBar;
	private Button btn_getChapter;

	private ArrayList<NovelChapterData.Chapter> chapterDataList;

	private int mBookId;
	private int mCurrentCid;
	private int mCurrentPosition = 0;
	private int mOldPosition = 0;

	private int DEFEAT_CODE;
	private final int GET_CHAPTER_DEFEAT = 0;
	private final int GET_CHAPTER_CONTENT_DEFEAT = 1;

	private boolean isChapterListOpen = true;

	private final long mDuration = 300;

	private ValueAnimator animatorDown;
	private ValueAnimator animatorUp;
	private Button btn_close;
	private RelativeLayout rl_chapter;
	private TextView tv_cname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_read);

		initView();

		setListener();

		initData();
	}

	private void setListener() {
		btn_getChapter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DEFEAT_CODE == GET_CHAPTER_DEFEAT){
					getBook();
				}else{
					getChapterContent();
				}

				progressBar.setVisibility(View.VISIBLE);
				btn_getChapter.setVisibility(View.INVISIBLE);
			}
		});

		tv_content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openChapterList();
			}
		});

		btn_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeChapterList();
			}
		});

		lv_chapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mOldPosition = mCurrentPosition;
				mCurrentPosition = position;
				mCurrentCid = chapterDataList.get(position).cid;

				//设置字体加粗
				TextView tv = (TextView) view.findViewById(R.id.tv_chapter);
				tv.getPaint().setFakeBoldText(true);
				tv.invalidate();

				closeChapterList();
				progressBar.setVisibility(View.VISIBLE);
				tv_content.setText("");
				getChapterContent();
			}
		});
	}

	private void initData() {
		Intent intent = getIntent();
		mBookId = intent.getIntExtra("bookId", 0);

		getBook();
	}

	private void initView() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_cname = (TextView) findViewById(R.id.tv_cname);
		lv_chapter = (ListView) findViewById(R.id.lv_chapter);
		btn_getChapter = (Button) findViewById(R.id.btn_getChapter);
		rl_chapter = (RelativeLayout) findViewById(R.id.rl_chapter);
		btn_close = (Button) findViewById(R.id.btn_close);

		rl_chapter.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (rl_chapter.getHeight() != 0) {
					initAnimator();
					closeChapterList();
					rl_chapter.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});

	}

	public void getBook() {
		OkHttpUtils.get()
				.url(Global.buildGetNovelChapterUrlByBookId(mBookId))
				.build()
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(OnlineReadActivity.this, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						Gson gson = new Gson();
						NovelChapterData novelChapterData = gson.fromJson(response, NovelChapterData.class);
						NovelChapterData.NovelChapterBody chapterBody = novelChapterData.showapi_res_body;
						NovelChapterData.BookData bookData = chapterBody.book;
						if (bookData != null) {
							chapterDataList = bookData.chapterList;
						} else {
							ToastUtils.showSingleShortToast(OnlineReadActivity.this, "获取小说章节失败!");
							DEFEAT_CODE = GET_CHAPTER_DEFEAT;
							progressBar.setVisibility(View.INVISIBLE);
							btn_getChapter.setVisibility(View.VISIBLE);
							return;
						}
						Collections.sort(chapterDataList, new Comparator<NovelChapterData.Chapter>() {
							@Override
							public int compare(NovelChapterData.Chapter lhs, NovelChapterData.Chapter rhs) {
								return lhs.cid - rhs.cid;
							}
						});
						progressBar.setVisibility(View.INVISIBLE);
						lv_chapter.setAdapter(new MyAdapter());
						mCurrentCid = chapterDataList.get(0).cid;
						getChapterContent();
					}
				});
	}

	private void getChapterContent() {
		OkHttpUtils.get()
				.url(Global.buildGetNovelChapterContentUrlById(mBookId, mCurrentCid))
				.build()
				.execute(new StringCallback() {

					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(OnlineReadActivity.this, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						System.out.println(response);
						Gson gson = new Gson();
						ChapterContentData chapterContentData = gson.fromJson(response, ChapterContentData.class);
						ChapterContentData.ChapterContentBody chapterContent = chapterContentData.showapi_res_body;

						if (chapterContent.cname == null) {
							ToastUtils.showSingleShortToast(OnlineReadActivity.this, "获取小说章节内容失败!");
							DEFEAT_CODE = GET_CHAPTER_CONTENT_DEFEAT;
							progressBar.setVisibility(View.INVISIBLE);
							btn_getChapter.setVisibility(View.VISIBLE);
							return;
						}

						progressBar.setVisibility(View.INVISIBLE);
						tv_cname.setText(chapterContent.cname);
						String text = chapterContent.txt.replaceAll("<br /><br />", "\n\r　　");
						text = text.replace("<br/><br/>", "\n\r　　");
						tv_content.setText("　　" + text);
					}
				});
	}

	private void initAnimator() {
		animatorDown = ValueAnimator.ofFloat(0, rl_chapter.getHeight());
		animatorDown.setTarget(rl_chapter);
		animatorDown.setDuration(mDuration);
		animatorDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				rl_chapter.setY((Float) animation.getAnimatedValue());
			}
		});

		animatorUp = ValueAnimator.ofFloat(rl_chapter.getHeight(), 0);
		animatorUp.setTarget(rl_chapter);
		animatorUp.setDuration(mDuration);
		animatorUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				rl_chapter.setY((Float) animation.getAnimatedValue());
			}
		});
	}

	private void closeChapterList() {
		if (isChapterListOpen) {
			animatorUp.end();
			isChapterListOpen = false;
			animatorDown.start();
		}
	}

	private void openChapterList() {
		if (!isChapterListOpen) {
			rl_chapter.setVisibility(View.VISIBLE);
			lv_chapter.setSelection(mCurrentPosition);
			animatorDown.end();
			isChapterListOpen = true;
			animatorUp.start();
		}
	}

	@Override
	public void onBackPressed() {
		if (isChapterListOpen){
			closeChapterList();
			return;
		}

		super.onBackPressed();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return chapterDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return chapterDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(OnlineReadActivity.this, R.layout.list_chaper_item, null);
				holder = new ViewHolder();
				holder.tv_chapter = (TextView) view.findViewById(R.id.tv_chapter);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_chapter.setText(chapterDataList.get(position).name);
			if(mCurrentPosition == position){
				holder.tv_chapter.getPaint().setFakeBoldText(true);
			}else{
				holder.tv_chapter.getPaint().setFakeBoldText(false);
			}
			holder.tv_chapter.invalidate();

			return view;
		}

		class ViewHolder {
			private TextView tv_chapter;
		}
	}
}
