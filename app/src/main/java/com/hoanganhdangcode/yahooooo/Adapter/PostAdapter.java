
package com.hoanganhdangcode.yahooooo.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Model.MediaPost;
import com.hoanganhdangcode.yahooooo.Model.UserPost;
import com.hoanganhdangcode.yahooooo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

            public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

                private final Context context;
                private final List<UserPost> postList;

                public PostAdapter(Context context, List<UserPost> postList) {
                    this.context = context;
                    this.postList = postList;
                }

                @NonNull
                @Override
                public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(context).inflate(R.layout.item_post_layout, parent, false);
                    return new PostViewHolder(view);
                }

                @Override
                public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
                    UserPost post = postList.get(position);

                    holder.name.setText(post.getUid());
                    holder.posttext.setText(post.getPosttext());

                    Glide.with(context).load(R.drawable.avartarerror).into(holder.avatar);

                    int scope = post.getScope();
                    if (scope == 0) holder.typepost.setImageResource(R.drawable.ic_lock);
                    else if (scope == 1) holder.typepost.setImageResource(R.drawable.ic_nav_profile);
                    else holder.typepost.setImageResource(R.drawable.ic_public);

                    List<MediaPost> mediaList = post.getPostlist();
                    int mediasize = mediaList.size();
                    // Hiển thị tối đa 3 media đầu tiên nếu có
                    holder.item1.setVisibility(GONE);
                    holder.item2.setVisibility(GONE);
                    holder.item3.setVisibility(GONE);
                    holder.item4.setVisibility(GONE);

                    if (mediasize>0){        holder.item1.setVisibility(VISIBLE);
                        if (!mediaList.get(0).getUrl().isEmpty())    Glide.with(context).load(mediaList.get(0).getUrl()).placeholder(R.drawable.avartarerror).into(holder.item1);

                        holder.progressbar1.setVisibility(mediaList.get(0).getUrl().isEmpty()?VISIBLE:GONE);

                    }
                    if (mediasize>1){        holder.item2.setVisibility(VISIBLE);
                        if (!mediaList.get(1).getUrl().isEmpty())    Glide.with(context).load(mediaList.get(1).getUrl()).placeholder(R.drawable.avartarerror).into(holder.item2);

                        holder.progressbar2.setVisibility(mediaList.get(1).getUrl().isEmpty()?VISIBLE:GONE);
                    }
                    if (mediasize>2){        holder.item1.setVisibility(VISIBLE);
                        if (!mediaList.get(2).getUrl().isEmpty())    Glide.with(context).load(mediaList.get(2).getUrl()).placeholder(R.drawable.avartarerror).into(holder.item3);

                        holder.progressbar3.setVisibility(mediaList.get(2).getUrl().isEmpty()?VISIBLE:GONE);
                    }
                    if (mediasize>3){        holder.item4.setVisibility(VISIBLE);
                        holder.item4.setText("+"+(mediasize-3));
                        holder.progressbar3.setVisibility(GONE);
                        holder.playvideo3.setVisibility(GONE);
                    }

                }

                @Override
                public int getItemCount() {
                    return postList.size();
                }

                public void submitList(List<UserPost> userPosts) {
                    postList.clear();
                    postList.addAll(userPosts);
                    notifyDataSetChanged();
                }


                public static class PostViewHolder extends RecyclerView.ViewHolder {
                    CircleImageView avatar;
                    TextView name, posttext, timestamp;
                    ImageButton typepost;
                    ImageView item1, item2, item3;
                    ImageView playvideo1, playvideo2, playvideo3;
                    ProgressBar progressbar1, progressbar2, progressbar3;
                    Button item4;

                    public PostViewHolder(@NonNull View itemView) {
                        super(itemView);
                        avatar = itemView.findViewById(R.id.avtar);
                        name = itemView.findViewById(R.id.name);
                        posttext = itemView.findViewById(R.id.posttext);
                        timestamp = itemView.findViewById(R.id.timestamp);
                        typepost = itemView.findViewById(R.id.typepost);
                        item1 = itemView.findViewById(R.id.item1);
                        item2 = itemView.findViewById(R.id.item2);
                        item3 = itemView.findViewById(R.id.item3);
                        progressbar1 = itemView.findViewById(R.id.progressbaritem1);
                        progressbar2 = itemView.findViewById(R.id.progressbaritem2);
                        progressbar3 = itemView.findViewById(R.id.progressbaritem3);
                        playvideo1 = itemView.findViewById(R.id.playvideo1);
                        playvideo2 = itemView.findViewById(R.id.playvideo2);
                        playvideo3 = itemView.findViewById(R.id.playvideo3);
                        item4 = itemView.findViewById(R.id.item4);
                    }
                }
            }

