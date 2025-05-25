package com.hoanganhdangcode.yahooooo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Model.FriendDisplay;
import com.hoanganhdangcode.yahooooo.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    public interface OnFriendActionListener {
        void onAdd(String uid, int status);
        void onCancel(String uid);
        void onChat(String uid,int status);
        void onViewProfile(String uid);
        void onBlock(String uid, int status);
    }

    private Context context;
    private List<FriendDisplay> list;
    private OnFriendActionListener listener;
    private String loadingUid = null;

    public FriendAdapter(Context context, List<FriendDisplay> list, OnFriendActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void setFriendList(List<FriendDisplay> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void showLoadingFor(String uid) {
        this.loadingUid = uid;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_layout, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendDisplay friend = list.get(position);

        holder.name.setText(friend.getName());
        holder.phone.setText(friend.getPhone());
        holder.status.setText(getStatusText(friend.getStatus()));
        Glide.with(context)
                .load(friend.getAvatar())
                .placeholder(R.drawable.avatardefault)
                .into(holder.avatar);

        int status = friend.getStatus();
        holder.btnAdd.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnChat.setVisibility(View.GONE);
        holder.btnViewProfile.setVisibility(View.VISIBLE);
        holder.btnBlock.setVisibility(View.VISIBLE);

        holder.btnAdd.setText("Thêm bạn");
        holder.btnBlock.setText("Chặn");

        if (status == -1) {
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnChat.setVisibility(View.VISIBLE);
        } else if (status == 3) {
            holder.btnChat.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setText("Hủy lời mời");

        } else if (status == 4) {
            holder.btnAdd.setText("Đồng ý");
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnChat.setVisibility(View.VISIBLE);
        } else if (status == 5) {
            holder.btnCancel.setText("Hủy bạn bè");
            holder.btnChat.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else if (status == 2) {
            holder.btnBlock.setText("Bỏ chặn");
        }

        boolean isLoading = friend.getUid().equals(loadingUid);
        holder.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        holder.btnAdd.setEnabled(!isLoading);
        holder.btnCancel.setEnabled(!isLoading);
        holder.btnChat.setEnabled(!isLoading);
        holder.btnViewProfile.setEnabled(!isLoading);
        holder.btnBlock.setEnabled(!isLoading);

        holder.btnAdd.setOnClickListener(v -> listener.onAdd(friend.getUid(), status));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(friend.getUid()));
        holder.btnChat.setOnClickListener(v -> listener.onChat(friend.getUid(),status));
        holder.btnViewProfile.setOnClickListener(v -> listener.onViewProfile(friend.getUid()));
        holder.btnBlock.setOnClickListener(v -> listener.onBlock(friend.getUid(), status));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView name, phone, status;
        Button btnAdd, btnCancel, btnChat, btnViewProfile, btnBlock;
        ProgressBar progressBar;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            status = itemView.findViewById(R.id.status);
            btnAdd = itemView.findViewById(R.id.btnadd);
            btnCancel = itemView.findViewById(R.id.btncancel);
            btnChat = itemView.findViewById(R.id.btnchat);
            btnViewProfile = itemView.findViewById(R.id.btnwall);
            btnBlock = itemView.findViewById(R.id.btnblock);
            progressBar = itemView.findViewById(R.id.progressbarfunction);
        }
    }

    private String getStatusText(int status) {
        switch (status) {
            case -1: return "Người lạ";
            case 1: return "Bị chặn";
            case 2: return "Đã chặn";
            case 3: return "Đã gửi lời mời";
            case 4: return "Lời mời đến";
            case 5: return "Bạn bè";
            default: return "Không rõ";
        }
    }
}