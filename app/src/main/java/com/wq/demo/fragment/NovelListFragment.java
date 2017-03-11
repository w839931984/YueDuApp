package com.wq.demo.fragment;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.wq.demo.R;
import com.wq.demo.activity.OnlineReadActivity;
import com.wq.demo.adapter.NovelListAdapter;
import com.wq.demo.bean.NovelBooksData;
import com.wq.demo.global.Global;
import com.wq.demo.utils.ToastUtils;
import com.wq.demo.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by WQ on 2016/10/14.
 */

public class NovelListFragment extends BaseFragment {

	private final String mType;
	private RecyclerView recView;
	private NovelListAdapter mAdapter;

	private int mPage = 1;
	private ArrayList<NovelBooksData.NovelBooks> mNovelBooks;

	public NovelListFragment(String type) {
		super();
		mType = type;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_novel_list, null);
		recView = (RecyclerView) view.findViewById(R.id.rec_view);
		return view;
	}

	@Override
	public void initData() {
		//设置布局管理器
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recView.setLayoutManager(linearLayoutManager);
		//添加分割线
		recView.addItemDecoration(
				new RecycleViewDivider(
						mActivity,
						LinearLayoutManager.HORIZONTAL,
						2,
						getResources().getColor(R.color.mainColor)
				)
		);
		// 设置item动画
		recView.setItemAnimator(new DefaultItemAnimator());

		recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			//用来标记是否正在向最后一个滑动
			boolean isSlidingToLast = false;

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
				// 当不滚动时
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					//获取最后一个完全显示的ItemPosition
					int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
					int totalItemCount = manager.getItemCount();

					// 判断是否滚动到底部，并且是向右滚动
					if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
						getData();
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				//dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                //大于0表示正在向右滚动
//小于等于0表示停止或向左滚动
                isSlidingToLast = dy > 0;
			}
		});
	}

	//设置适配器
	public void setAdapter(NovelListAdapter adapter) {
		if (adapter != null) {
			mAdapter = adapter;
		}
		recView.setAdapter(mAdapter);
	}

	public void notifyData() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	public void getData() {
		OkHttpUtils.get()
				.url(Global.buildNovelListUrl(mType, "", mPage + ""))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(mActivity, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						mPage++;
						Gson gson = new Gson();
						NovelBooksData novelBooksData = gson.fromJson(response, NovelBooksData.class);
						if (novelBooksData != null) {

							ArrayList<NovelBooksData.NovelBooks> list = novelBooksData.showapi_res_body.pagebean.contentlist;
							for (int i = 0; i < list.size(); i++) {
								list.get(i).name = list.get(i).name.replaceAll(" ", "").replaceAll("下载", "");
							}

							if (mNovelBooks == null) {
								mNovelBooks = list;
							} else {
								mNovelBooks.addAll(list);
							}

							if (!hasData()) {
								mAdapter = new NovelListAdapter(mActivity, mNovelBooks);

								mAdapter.setOnRecyclerViewItemClickListener(new NovelListAdapter.OnRecyclerViewItemClickListener() {
									@Override
									public void onItemClick(View view, int position) {
										ToastUtils.showSingleShortToast(mActivity, position + "");
										if(mNovelBooks.get(position).id == 0){
											ToastUtils.showSingleShortToast(mActivity, "获取书籍Id失败！");
											return;
										}
										Intent intent = new Intent(mActivity, OnlineReadActivity.class);
										intent.putExtra("bookId", mNovelBooks.get(position).id);
										startActivity(intent);
									}

									@Override
									public void onItemLongClick(View view, int position) {

									}
								});
								recView.setAdapter(mAdapter);
							} else {
								mAdapter.setList(mNovelBooks);
								mAdapter.notifyDataSetChanged();
							}

						}
					}
				});
	}

	public boolean hasData() {
		return mAdapter != null;
	}
}
