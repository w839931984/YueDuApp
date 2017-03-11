package com.wq.demo.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.viewpagerindicator.TabPageIndicator;
import com.wq.demo.R;
import com.wq.demo.activity.MainActivity;
import com.wq.demo.bean.NovelKindsData;
import com.wq.demo.global.Global;
import com.wq.demo.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;


/**
 * Created by WQ on 2016/9/21.
 */
public class OnlineReadFragment extends BaseFragment {

	private ViewPager mViewPager;

	private ArrayList<NovelKindsData.NovelKind> novelKinds;
	private ArrayList<NovelListFragment> fragments;

	private TabPageIndicator mTabPageIndicator;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.framlayout_online_read, null);
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager_novel);
		mTabPageIndicator = (TabPageIndicator) view.findViewById(R.id.view_pager_indicator_novel);
		return view;
	}

	@Override
	public void initData() {
		getNovelKinds();

		mTabPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (position != 0) {
					((MainActivity) mActivity).getDragView().setDragenable(false);
				} else {
					((MainActivity) mActivity).getDragView().setDragenable(true);
				}

				if(!fragments.get(position).hasData()){
					System.out.println("没有获取到数据");
					fragments.get(position).getData();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	public ArrayList<String> getNovelKinds() {
		OkHttpUtils.get()
				.url(Global.URL_NOVEL_KIND)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showSingleShortToast(mActivity, "请求失败！");
					}

					@Override
					public void onResponse(String response, int id) {
						Gson gson = new Gson();
						NovelKindsData novelKindsData = gson.fromJson(response, NovelKindsData.class);

						novelKinds = novelKindsData.showapi_res_body.typeList;
						fragments = new ArrayList<>();

						for (int i = 0; i < novelKinds.size(); i++) {
							String name = novelKinds.get(i).name;
							novelKinds.get(i).name = name.replaceAll(" ", "");
							fragments.add(new NovelListFragment(novelKinds.get(i).id));
						}

//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
						FragmentManager fragmentManager = ((MainActivity) mActivity).getSupportFragmentManager();
						mViewPager.setAdapter(new FragmentAdapter(fragmentManager));
						mTabPageIndicator.setViewPager(mViewPager);
						mTabPageIndicator.setVisibility(View.VISIBLE);

						fragments.get(0).getData();
//							}
//						});
					}
				});

		return null;
	}

	class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return novelKinds.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
//			TextView textView = new TextView(mActivity);
//			textView.setText(novelKinds.get(position).name);
			NovelListFragment fragment = new NovelListFragment(novelKinds.get(position).id);
			View view = fragment.getView();
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return novelKinds.get(position).name;
		}
	}

	public class FragmentAdapter extends FragmentPagerAdapter {

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return novelKinds.get(position).name;
		}
	}
}
