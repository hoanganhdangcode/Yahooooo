package com.hoanganhdangcode.yahooooo.Adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Model.Video;
import com.hoanganhdangcode.yahooooo.R;

import java.util.List;

public class VideoAdapter extends  RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private Activity mActivity;
    private List<Video> mListVideos;

    public VideoAdapter(Activity activity) {
        mActivity = activity;
    }
    public void setdata(List<Video> mListVideos){
        this.mListVideos = mListVideos;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.item_media_temp_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = mListVideos.get(position);
        if (video == null) {return;}
        Glide.with(holder.itemView.getContext()).load(video.getThumbnail()).into(holder.videoview);

    }

    @Override
    public int getItemCount() {
        return mListVideos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView videoview;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }



}


