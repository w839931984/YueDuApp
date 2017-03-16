package com.wq.demo.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.wq.demo.R;
import com.wq.demo.adapter.HappyListAdapter;
import com.wq.demo.bean.JokeData;
import com.wq.demo.global.Global;
import com.wq.demo.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * Created by WQ on 2016/9/21.
 */
public class HappyReadFragment extends BaseFragment {

	private static final String TAG = "com.wq.yuedu";
	private RecyclerView recHappy;

	private List<JokeData.Joke> mJokeList = new ArrayList<>();
	private List<JokeData.Joke> mJokeListTemp = new ArrayList<>();

	private int mJokePage_1 = 1;
	private int mJokePage_2 = 1;
	private int mJokePage_3 = 1;

	private int mJokeType = 1;
	private HappyListAdapter adapter;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.framlayout_happy, null);
		recHappy = (RecyclerView) view.findViewById(R.id.rec_happy);
		return view;
	}

	@Override
	public void initData() {
		//设置布局管理器
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
		recHappy.setLayoutManager(layoutManager);
		// 设置item动画
		recHappy.setItemAnimator(new DefaultItemAnimator());
		adapter = new HappyListAdapter(mJokeList);
		recHappy.setAdapter(adapter);
		getData();
	}

	private void getData() {
		OkHttpUtils.get()
				.url(Global.buildGetJokeDataUrl(mJokeType, mJokePage_1))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(mActivity, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						Gson gson = new Gson();
						JokeData jokeData = gson.fromJson(response, JokeData.class);
						mJokeListTemp.addAll(jokeData.showapi_res_body.contentlist);
						if (mJokeType == 3){
							Collections.shuffle(mJokeListTemp);
							mJokeList.addAll(mJokeListTemp);
							adapter.setJokeList(mJokeList);
							adapter.notifyDataSetChanged();
							mJokeType = 1;
							mJokePage_1++;
							mJokePage_2++;
							mJokePage_3++;
							return;
						}
						mJokeType++;
						getData();
					}
				});
	}


}
