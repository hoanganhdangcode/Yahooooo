package com.hoanganhdangcode.yahooooo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Model.ChatDisplay;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(String chatId);
    }

    private final Context context;
    private List<ChatDisplay> chatList;
    private final OnChatClickListener listener;


    public ChatAdapter(Context context, List<ChatDisplay> chatList, OnChatClickListener listener) {
        this.context = context;
        this.chatList = chatList;
        this.listener = listener;
    }
    public void setChatList(List<ChatDisplay> list) {
        this.chatList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_layout, parent, false);
        return new ChatViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDisplay chat = chatList.get(position);

        holder.name.setText(chat.getName());
        holder.status.setText(getTypeLabel(chat.getType()));
        holder.lastUser.setText(chat.getUserlast() != null ? (chat.getUserlast()+":"):(""));
        holder.lastMessage.setText(chat.getLastmessage());
        holder.timestamp.setText(Utils.formatTime(chat.getLasttimestamp()));
        Glide.with(context)
                .load(chat.getAvatar())
                .placeholder(R.drawable.avatardefault)
                .into(holder.avatar);
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat.getChatid()));
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView name, status, lastUser, lastMessage, timestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            lastUser = itemView.findViewById(R.id.lastuser);
            lastMessage = itemView.findViewById(R.id.lastmessage);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    private String getTypeLabel(int type) {
        switch (type) {
            case 1: return "Bạn bè";
            case 2: return "Nhóm";
            case 0: return "Người lạ";
            case -1: return "Chặn";
            default: return "Không rõ";
        }
    }


}
