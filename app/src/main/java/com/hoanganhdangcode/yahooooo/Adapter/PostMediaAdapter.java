package com.hoanganhdangcode.yahooooo.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Model.MediaPost;
import com.hoanganhdangcode.yahooooo.R;

import java.util.ArrayList;
import java.util.List;

public class PostMediaAdapter extends RecyclerView.Adapter<PostMediaAdapter.MediaViewHolder> {

    private final Context context;
    private final List<MediaPost> mediaList;

    public PostMediaAdapter(Context context, List<MediaPost> mediaList) {
        this.context = context;
        this.mediaList = mediaList != null ? mediaList : new ArrayList<>();
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media_temp_layout, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaPost media = mediaList.get(position);

        boolean isVideo = media.getUrl().endsWith(".mp4") || media.getUrl().contains("video");

        if (!media.getUrl().isEmpty()) {
            Glide.with(context).load(Uri.parse(media.getLocalUri())).placeholder(R.drawable.imgblack).into(holder.thumbnail);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            holder.thumbnail.setImageResource(R.color.black);
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        holder.playIcon.setVisibility(isVideo ? View.VISIBLE : View.GONE);

        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                mediaList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, mediaList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public void updateList(List<MediaPost> newList) {
        mediaList.clear();
        if (newList != null) {
            mediaList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, playIcon;
        ProgressBar progressBar;
        ImageButton deleteButton;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            playIcon = itemView.findViewById(R.id.playvideo);
            progressBar = itemView.findViewById(R.id.progressbar);
            deleteButton = itemView.findViewById(R.id.delete);
        }
    }
}
