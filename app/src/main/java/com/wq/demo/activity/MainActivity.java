package com.wq.demo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wq.demo.R;
import com.wq.demo.drag.DragFrameLayout;
import com.wq.demo.fragment.HappyReadFragment;
import com.wq.demo.fragment.MainLeftFragment;
import com.wq.demo.fragment.NativeReadFragment;
import com.wq.demo.fragment.OnlineReadFragment;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private Toolbar mToolBar;
	private DragFrameLayout mDragView;
	private RadioGroup mRadioGroup;
	private ActionBar mAppBar;
	private NativeReadFragment mNativeReadFragment;

	private ArrayList<Fragment> mFragments;
	private FragmentManager mFragmentManager;

	private final String FRAGMENT_NATIVE_READ = "fragment_native_read";
	private final String FRAGMENT_ONLINE_READ = "fragment_online_read";
	private final String FRAGMENT_CARTOON_READ = "fragment_cartoon_read";
	private final String FRAGMENT_MAIN_LEFT = "fragment_main_left";
	private OnlineReadFragment mOnlineReadFragment;
	private HappyReadFragment mHappyReadFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
//			View decorView = getWindow().getDecorView();
//			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//			decorView.setSystemUiVisibility(option);
//			getWindow().setStatusBarColor(Color.TRANSPARENT);
//		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
//			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//		}

		//初始化控件
		initView();

		//初始化toolbar
		initAppBar();

		setListener();

		initData();
	}

	private void initView() {
		mToolBar = (Toolbar) findViewById(R.id.toolbar);
		mDragView = (DragFrameLayout) findViewById(R.id.drag);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg);
	}

	private void initAppBar() {
		mToolBar.setTitle("悦读");
		mToolBar.setTitleTextColor(Color.WHITE);
		mToolBar.setNavigationIcon(R.drawable.ic_action_storage);

		setSupportActionBar(mToolBar);

		mAppBar = getSupportActionBar();
	}

	private void setListener() {
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				FragmentTransaction transaction = mFragmentManager.beginTransaction();
				setFragmentVisible(transaction);
				switch (checkedId){
					case R.id.rb_native:
						Log.i("WQ", "切换到本地");
						mAppBar.setTitle("书架");
						if (mNativeReadFragment == null) {
							Log.i("WQ", "创建本地页面");
							mNativeReadFragment = new NativeReadFragment();
							mFragments.add(mNativeReadFragment);
							transaction.add(R.id.right_fl, mNativeReadFragment, FRAGMENT_NATIVE_READ);
						}
						transaction.show(mNativeReadFragment);
						break;
					case R.id.rb_online:
						Log.i("WQ", "切换到在线");
						mAppBar.setTitle("在线阅读");
						if (mOnlineReadFragment == null) {
							Log.i("WQ", "创建在线页面");
							mOnlineReadFragment = new OnlineReadFragment();
							mFragments.add(mOnlineReadFragment);
							transaction.add(R.id.right_fl, mOnlineReadFragment, FRAGMENT_ONLINE_READ);
						}
						transaction.show(mOnlineReadFragment);
						break;
					case R.id.rb_happy:
						Log.i("WQ", "切换到开心一笑");
						mAppBar.setTitle("开心一笑");
						if (mHappyReadFragment == null) {
							Log.i("WQ", "创建开心一笑页面");
							mHappyReadFragment = new HappyReadFragment();
							mFragments.add(mHappyReadFragment);
							transaction.add(R.id.right_fl, mHappyReadFragment, FRAGMENT_CARTOON_READ);
						}
						transaction.show(mHappyReadFragment);
						break;
				}
				transaction.commit();
			}
		});
	}

	private void initData() {
		mFragmentManager = getSupportFragmentManager();

		mFragments = new ArrayList<>();

		//初始化mRadioGroup的默认选择
		mRadioGroup.check(R.id.rb_native);
		mAppBar.setTitle("书架");

		initMainLeft();
	}

	private void initMainLeft() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(R.id.main_left_fragment, new MainLeftFragment(), FRAGMENT_MAIN_LEFT);
		transaction.commit();
	}

	private void setFragmentVisible(FragmentTransaction transaction) {
		for (Fragment fragment : mFragments) {
			if (fragment != null){
				transaction.hide(fragment);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		initSearchView(menu);

		return true;
	}

	private void initSearchView(Menu menu) {
		final MenuItem item = menu.findItem(R.id.search);
		final SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);
		ImageView searchButton = (ImageView)mSearchView.findViewById(R.id.search_button);
		searchButton.setImageResource(R.drawable.ic_action_search);
		mSearchView.setQueryHint("请输入书籍名称");

		mSearchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchView.setBackgroundColor(Color.WHITE);
			}
		});

		mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				mSearchView.setBackgroundColor(Color.TRANSPARENT);
				return false;
			}
		});

		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Toast.makeText(MainActivity.this, "开始搜索", Toast.LENGTH_SHORT).show();
				mSearchView.onActionViewCollapsed();
				mSearchView.setBackgroundColor(Color.TRANSPARENT);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.search:
				break;
			case R.id.setting:
				startActivity(new Intent(this, SettingActivity.class));
				break;
			default:
				mDragView.change();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

//		设置完全的沉浸式状态栏
//		if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//			View decorView = getWindow().getDecorView();
//			decorView.setSystemUiVisibility(
//					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//							| View.SYSTEM_UI_FLAG_FULLSCREEN
//							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//		}
	}

	public DragFrameLayout getDragView() {
		return mDragView;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		OkHttpUtils.getInstance().cancelAll();
	}
}
