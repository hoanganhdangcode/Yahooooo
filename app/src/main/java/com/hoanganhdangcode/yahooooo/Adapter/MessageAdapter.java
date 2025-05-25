package com.hoanganhdangcode.yahooooo.Adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Model.UserMessage;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;

    private final Context context;
    private final String currentUid;
    private final HashMap<String, String> nameMap;
    private final HashMap<String, String> avatarMap;
    private List<UserMessage> messages;

    public MessageAdapter(Context context, String currentUid, HashMap<String, String> nameMap, HashMap<String, String> avatarMap) {
        this.context = context;
        this.currentUid = currentUid;
        this.nameMap = nameMap;
        this.avatarMap = avatarMap;
    }

    public void setMessages(List<UserMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender().equals(currentUid) ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_SENT) {
            View view = inflater.inflate(R.layout.send_mess_item, parent, false); // đúng layout
            return new SentViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.receive_mess_item, parent, false); // đúng layout
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserMessage message = messages.get(position);
        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).bind(message);
        } else {
            ((ReceivedViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }
    class SentViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp,statusmess;
        ImageView messageImage, messageVideoThumbnail;
        ImageButton playButton;
        ProgressBar progressBarImage, progressBarVideo;

        SentViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messagetxt);
            timestamp = itemView.findViewById(R.id.timestamp);
            messageImage = itemView.findViewById(R.id.messageimg);
            messageVideoThumbnail = itemView.findViewById(R.id.messagevideo);
            playButton = itemView.findViewById(R.id.messagevideobtn);
            progressBarImage = itemView.findViewById(R.id.progressbarimg);
            statusmess = itemView.findViewById(R.id.statusmess);
            progressBarVideo = itemView.findViewById(R.id.progressbarvideo);
        }

        void bind(UserMessage message) {
            timestamp.setText(Utils.formatTime(message.getTimestamp()));
            messageText.setVisibility(GONE);
            messageImage.setVisibility(GONE);
            messageVideoThumbnail.setVisibility(GONE);
            playButton.setVisibility(GONE);
            progressBarImage.setVisibility(GONE);
            progressBarVideo.setVisibility(GONE);
            statusmess.setVisibility(GONE);
            int type = message.getType();
            int status = message.getStatus();
            switch (type) {
                case 1:
                    messageText.setVisibility(VISIBLE);
                    messageText.setText(status==4?"Tin nhắn đã bị xóa":(status==-1?"lỗi":message.getContent()));
                    statusmess.setVisibility(VISIBLE);
                    statusmess.setText(statusm(status));
                    break;
                case 2:
                    messageImage.setVisibility((status==-1||status==4)?GONE:VISIBLE);
                    messageText.setText(status==4?"Tin nhắn đã bị xóa":(status==-1?"lỗi":message.getContent()));
                    messageText.setVisibility(status==4||status==-1?VISIBLE:GONE);
                    statusmess.setVisibility(VISIBLE);
                    statusmess.setText(statusm(status));
                    progressBarImage.setVisibility(status==0?VISIBLE:GONE);
                    Glide.with(context)
                            .load(message.getContent())
                            .placeholder(R.drawable.imgblack)
                            .into(messageImage);
                    messageImage.setOnClickListener(v -> openImg(message.getContent()));
                    break;
                case 3:
                    messageVideoThumbnail.setVisibility((status==-1||status==4)?GONE:VISIBLE);
                    playButton.setVisibility((status<=0||status==4)?GONE:VISIBLE);
                    messageText.setText(status==4?"Tin nhắn đã bị xóa":(status==-1?"lỗi":message.getContent()));
                    messageText.setVisibility(status==4||status==-1?VISIBLE:GONE);
                    statusmess.setVisibility(VISIBLE);

                    statusmess.setText(statusm(status));
                    progressBarImage.setVisibility(status==0?VISIBLE:GONE);
                    Glide.with(context)
                            .load(message.getContent())
                            .placeholder(R.drawable.imgblack)
                            .into(messageVideoThumbnail);
                    messageVideoThumbnail.setOnClickListener(v -> openVideo(message.getContent()));
                    playButton.setOnClickListener(v -> openVideo(message.getContent()));
                    break;
            }
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText, timestamp,statusmess;
        CircleImageView avatar;
        ImageView messageImage, messageVideoThumbnail;
        ImageButton playButton;
        ProgressBar progressBarImage, progressBarVideo;

        ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messagetxt);
            nameText = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            avatar = itemView.findViewById(R.id.uimg);
            messageImage = itemView.findViewById(R.id.messageimg);
            messageVideoThumbnail = itemView.findViewById(R.id.messagevideo);
            playButton = itemView.findViewById(R.id.messagevideobtn);
            progressBarImage = itemView.findViewById(R.id.progressbarimg);
            progressBarVideo = itemView.findViewById(R.id.progressbarvideo);
            statusmess = itemView.findViewById(R.id.statusmess);
        }

        void bind(UserMessage message) {
            timestamp.setText(Utils.formatTime(message.getTimestamp()));
            String sender = message.getSender();
            nameText.setText(nameMap.containsKey(sender) ? nameMap.get(sender) : "Không xác định");
            Glide.with(context)
                    .load(avatarMap.containsKey(sender) ? avatarMap.get(sender) : R.drawable.avatardefault)
                    .placeholder(R.drawable.imgblack)
                    .into(avatar);

            messageText.setVisibility(GONE);
            messageImage.setVisibility(GONE);
            messageVideoThumbnail.setVisibility(GONE);
            playButton.setVisibility(GONE);
            progressBarImage.setVisibility(GONE);
            progressBarVideo.setVisibility(GONE);
            statusmess.setVisibility(GONE);
            int type = message.getType();
            int status = message.getStatus();
            switch (type) {
                case 1:
                    messageText.setVisibility(status==-1?GONE:VISIBLE);
                    messageText.setText(status==4?"Tin nhắn đã bị xóa":message.getContent());
                    statusmess.setVisibility(VISIBLE);
                    statusmess.setText(statusm(message.getStatus()));
                    break;
                case 2:
                    messageImage.setVisibility((status==-1||status==4)?GONE:VISIBLE);
                    messageText.setText(status==4?"Tin nhắn đã bị xóa":message.getContent());
                    messageText.setVisibility(status==4||status==-1?VISIBLE:GONE);
                    statusmess.setVisibility(VISIBLE);

                    statusmess.setText(statusm(status));
                    progressBarImage.setVisibility(status==0?VISIBLE:GONE);
                    Glide.with(context)
                            .load(message.getContent())
                            .placeholder(R.drawable.imgblack)
                            .into(messageImage);
                    messageImage.setOnClickListener(v -> openImg(message.getContent()));
                    break;
                case 3:
                    messageVideoThumbnail.setVisibility((status==-1||status==4)?GONE:VISIBLE);
                    playButton.setVisibility((status<=0||status==4)?GONE:VISIBLE);
                    messageText.setText(status==4?"Tin nhắn đã bị xóa":message.getContent());
                    messageText.setVisibility(status==4||status==-1?VISIBLE:GONE);
                    statusmess.setVisibility(VISIBLE);

                    statusmess.setText(statusm(status));
                    progressBarImage.setVisibility(status==0?VISIBLE:GONE);
                    Glide.with(context)
                            .load(message.getContent())
                            .placeholder(R.drawable.imgblack)
                            .into(messageVideoThumbnail);

                    messageVideoThumbnail.setOnClickListener(v -> openVideo(message.getContent()));
                    playButton.setOnClickListener(v -> openVideo(message.getContent()));
                    break;
            }
        }
    }

    private void openVideo(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void openImg(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private String statusm(int status){
        if(status ==-1) return "lỗi";
        if(status ==0) return "đang gửi";
        if(status ==1) return "đã gửi";
        if(status ==2) return "đã xem";
        if(status ==3) return "đã chỉnh sửa";
        if(status ==4) return "đã xóa";
        return "không rõ";
    }
}