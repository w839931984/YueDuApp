package com.wq.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wq.demo.R;
import com.wq.demo.bean.JokeData;

import java.util.List;

/**
 * Created by WQ on 2017/3/16.
 */

public class HappyListAdapter extends RecyclerView.Adapter<HappyListAdapter.ViewHolder> {
    private Context mContext;
    private List<JokeData.Joke> mJokeList;

    public HappyListAdapter(List<JokeData.Joke> jokeList) {
        mJokeList = jokeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_joke_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JokeData.Joke joke = mJokeList.get(position);
        if (joke.type == 1){
            holder.tvJoke.setText(joke.text.trim());
            holder.ivJoke.setVisibility(View.GONE);
        } else {
            holder.ivJoke.setVisibility(View.VISIBLE);
            holder.tvJoke.setText(joke.title);
            Glide.with(mContext).load(joke.img).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivJoke);
        }

    }

    @Override
    public int getItemCount() {
        return mJokeList.size();
    }

    public List<JokeData.Joke> getJokeList() {
        return mJokeList;
    }

    public void setJokeList(List<JokeData.Joke> jokeList) {
        this.mJokeList = jokeList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivJoke;
        TextView tvJoke;

        public ViewHolder(View itemView) {
            super(itemView);

            ivJoke = (ImageView) itemView.findViewById(R.id.iv_joke);
            tvJoke = (TextView) itemView.findViewById(R.id.tv_joke);
        }
    }
}
