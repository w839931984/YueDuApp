package com.wq.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wq.demo.R;
import com.wq.demo.bean.NovelBooksData;

import java.util.ArrayList;

/**
 * Created by WQ on 2016/10/14.
 */

public class NovelListAdapter extends RecyclerView.Adapter<NovelListAdapter.MyViewHolder> {

	private ArrayList<NovelBooksData.NovelBooks> mList;
	private final LayoutInflater mInflater;
//	private final Context mContext;

	public interface OnRecyclerViewItemClickListener {
		void onItemClick(View view, int position);

		void onItemLongClick(View view, int position);
	}

	private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

	public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener) {
		this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
	}

	public NovelListAdapter(Context context, ArrayList<NovelBooksData.NovelBooks> list) {
//		mContext = context;
		mInflater = LayoutInflater.from(context);
		mList = list;
	}

	public void setList(ArrayList<NovelBooksData.NovelBooks> list) {
		this.mList = list;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.list_online_novel_itme, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		holder.novelNameTextView.setText(mList.get(position).name);
		holder.novelAuthorTextView.setText("作者：" + mList.get(position).author);
		holder.novelNewChapterTextView.setText("最新章节：" + mList.get(position).newChapter);
		holder.novelLastTimeTextView.setText("最后更新：" + mList.get(position).updateTime);

		if (mOnRecyclerViewItemClickListener != null) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = holder.getLayoutPosition();
					mOnRecyclerViewItemClickListener.onItemClick(holder.itemView, pos);
				}
			});

			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int pos = holder.getLayoutPosition();
					return false;
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		private final ImageView novelImageView;
		private final TextView novelNameTextView;
		private final TextView novelAuthorTextView;
		private final TextView novelNewChapterTextView;
		private final TextView novelLastTimeTextView;
		private final View itemView;

		public MyViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
			novelImageView = (ImageView) itemView.findViewById(R.id.iv_novel_image);
			novelNameTextView = (TextView) itemView.findViewById(R.id.tv_novel_name);
			novelAuthorTextView = (TextView) itemView.findViewById(R.id.tv_novel_author);
			novelNewChapterTextView = (TextView) itemView.findViewById(R.id.tv_novel_newChapter);
			novelLastTimeTextView = (TextView) itemView.findViewById(R.id.tv_novel_lastTime);
		}
	}
}
