package com.wq.demo.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wq.demo.R;

/**
 * Created by WQ on 2016/9/21.
 */
public class HappyReadFragment extends BaseFragment {

	private RecyclerView recHappy;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.framlayout_happy, null);
		recHappy = (RecyclerView) view.findViewById(R.id.rec_happy);
		return view;
	}

	@Override
	public void initData() {

	}
}
