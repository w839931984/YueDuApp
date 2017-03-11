package com.wq.demo.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wq.demo.R;
import com.wq.demo.activity.NativeReadActivity;
import com.wq.demo.bean.Book;
import com.wq.demo.db.BookDB;
import com.wq.demo.utils.FileUtils;
import com.wq.demo.utils.ToastUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by WQ on 2016/9/21.
 */
public class NativeReadFragment extends BaseFragment implements View.OnClickListener {

	private ArrayList<String> mFileList = new ArrayList<>();

	private int mFileMinSize = 100;

	private final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;
	private final int READ_ACTIVITY_REQUEST_CODE = 0;
	private final int SEARCH_FILE_COMPLETE = 100;

	private View mView;
	private Button mSearchButton;
	private ListView mListView;

	private SearchStatus mSearchStatus = SearchStatus.UnSearching;
	private ProgressBar mProgressBar;
	private BookDB bookDB;
	private int lastPosition;
	private Thread searchFileThread;

	private enum SearchStatus{
		Searching, UnSearching
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case SEARCH_FILE_COMPLETE:
					mSearchStatus = SearchStatus.UnSearching;
					if (mFileList.size() > 0){
						ToastUtils.showSingleShortToast(mActivity, "搜索完成！");
						mSearchButton.setVisibility(View.GONE);
						mProgressBar.setVisibility(View.GONE);
						mAdapter.notifyDataSetChanged();
						new Thread(new Runnable() {
							@Override
							public void run() {
								for (String path : mFileList) {
									Book book = new Book();
									book.setPath(path);
									bookDB.saveBook(book);
								}
							}
						}).start();
					}else{
						ToastUtils.showSingleShortToast(mActivity, "搜索完成，没有发现文本文件！");
						mSearchButton.setVisibility(View.VISIBLE);
						mProgressBar.setVisibility(View.INVISIBLE);
					}
					break;
			}
		}
	};
	private MyAdapter mAdapter;

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mFileList.size();
		}

		@Override
		public Object getItem(int position) {
			return mFileList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.list_native_novel_itme, null);
				holder = new ViewHolder();
				holder.novel_image = (ImageView) convertView.findViewById(R.id.iv_novel_image);
				holder.novel_name = (TextView) convertView.findViewById(R.id.tv_novel_name);
				holder.file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
				holder.progress = (TextView) convertView.findViewById(R.id.tv_progress);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String filePath = mFileList.get(position);
			File file = new File(filePath);
			holder.novel_name.setText(file.getName());
			holder.file_size.setText(FileUtils.convertFileSize(file.length()));

			Book book = bookDB.loadBook(filePath);
			if (book != null) {
				double progress = (double) bookDB.loadProgress(book.getId()) / file.length();
				System.out.println(bookDB.loadProgress(book.getId()));
				String format = new DecimalFormat("#.##").format(progress);
				holder.progress.setText(format + "%");
			}else {
				holder.progress.setText("0%");
			}
			return convertView;
		}

		public ViewHolder getViewHolderByPosition(int position){
			return (ViewHolder)((View)getItem(position)).getTag();
		}

		class ViewHolder{
			private ImageView novel_image;
			private TextView novel_name;
			private TextView file_size;
			private TextView progress;
		}
	}

	@Override
	public View initView() {
		mView = View.inflate(mActivity, R.layout.framlayout_native_read, null);
		return mView;
	}

	@Override
	public void initData() {
		bookDB = BookDB.getInstance(mActivity);

		mSearchButton = (Button)mView.findViewById(R.id.btn_search);
		mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
		mListView = (ListView)mView.findViewById(R.id.lv_native);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);

		ArrayList<Book> books = bookDB.loadAllBook();
		if (books.size() == 0) {
			mSearchButton.setVisibility(View.VISIBLE);
		} else {
			mFileList.clear();
			for (Book book : books) {
				mFileList.add(book.getPath());
			}
			mAdapter.notifyDataSetChanged();
		}

		mSearchButton.setOnClickListener(this);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				lastPosition = position;
				gotoReadActivity(mFileList.get(position));
			}
		});
	}

	public void gotoReadActivity(String path){
		Intent intent = new Intent(mActivity, NativeReadActivity.class);
		intent.putExtra("path", path);
		startActivityForResult(intent, READ_ACTIVITY_REQUEST_CODE);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_search:
				if (mSearchStatus == SearchStatus.Searching){
					ToastUtils.showSingleShortToast(mActivity, "据说多点几下会更快！");
					return;
				}
				mSearchStatus = SearchStatus.Searching;
				//在android6.0中申请权限
				if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED) {
					//申请READ_EXTERNAL_STORAGE权限
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
					}
				} else {
					searchFiles();
				}
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.i("WQ", "请求权限回调函数运行");
		if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				ToastUtils.showSingleShortToast(mActivity, "开始搜索文件！");
				searchFiles();
			} else {
				ToastUtils.showSingleShortToast(mActivity, "需要读取SD卡权限！");
				mSearchStatus = SearchStatus.UnSearching;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == READ_ACTIVITY_REQUEST_CODE){
			mAdapter.notifyDataSetChanged();
		}
	}

	private void searchFiles(){
		mSearchButton.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		//扫描文件逻辑
		searchFileThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//扫描文件逻辑
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					String path = Environment.getExternalStorageDirectory().toString();
					mFileList.clear();
					mFileList = FileUtils.getFiles(mFileList, path, "txt", mFileMinSize, true);
					Log.i("WQ", mFileList.toString());
					Message msg = Message.obtain();
					msg.what = SEARCH_FILE_COMPLETE;
					mHandler.sendMessage(msg);
				}
			}
		});
		searchFileThread.start();
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mSearchStatus == SearchStatus.Searching){
			searchFileThread.interrupt();
			mSearchButton.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}
}
