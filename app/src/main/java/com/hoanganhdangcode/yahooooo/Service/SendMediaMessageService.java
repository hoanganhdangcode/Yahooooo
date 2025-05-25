package com.hoanganhdangcode.yahooooo.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.Manifest;
import android.util.Log;


import com.google.firebase.database.FirebaseDatabase;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Repository.CloudinaryUploader;
import com.hoanganhdangcode.yahooooo.Util.Utils;

public class SendMediaMessageService extends Service {
    public static final String ACTION_UPLOAD = "SEND_MEDIA_MESSAGE";
    public static final String EXTRA_URI = "local_uri";
    public static final String EXTRA_CHAT_ID = "chat_id";
    public static final String EXTRA_MESSAGE_ID = "message_id";
    private static final String CHANNEL_ID = "send_media_channel";

    private void sendUploadBroadcast(String status, String chatId, String messageId, String info) {
        Intent intent = new Intent("com.hoanganhdangcode.UPLOAD_MEDIA_MESSAGE_RESULT");
        intent.putExtra("status", status); // "success" | "failed"
        intent.putExtra("chat_id", chatId);
        intent.putExtra("message_id", messageId);
        intent.putExtra("info", info); // url nếu thành công, error nếu lỗi
        sendBroadcast(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_UPLOAD.equals(intent.getAction())) {
            startForeground(1, createNotification("Đang gửi tin nhắn đa phương tiện..."));
            Uri localUri = intent.getParcelableExtra(EXTRA_URI);
            String chatId = intent.getStringExtra(EXTRA_CHAT_ID);
            String messageId = intent.getStringExtra(EXTRA_MESSAGE_ID);
            Log.d("SendMediaMessageService", "onStartCommand: " + localUri);

            CloudinaryUploader.uploadAuto(this, localUri, new CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String url) {
                    FirebaseDatabase.getInstance()
                            .getReference("messages")
                            .child(chatId)
                            .child(messageId)
                            .child("content")
                            .setValue(url);
                    FirebaseDatabase.getInstance()
                            .getReference("messages")
                            .child(chatId)
                            .child(messageId)
                            .child("status")
                            .setValue(1);
                    sendUploadBroadcast("success", chatId, messageId, url);
                    stopSelf();
                }

                @Override
                public void onFailure(String error) {
                    FirebaseDatabase.getInstance()
                            .getReference("messages")
                            .child(chatId)
                            .child(messageId)
                            .child("status")
                            .setValue(-1);
                    sendUploadBroadcast("failure", chatId, messageId, error);
                    stopSelf();
                }
            });
        }

        return START_NOT_STICKY;
    }

    private Notification createNotification(String contentText) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Gửi tin nhắn media", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Gửi tin nhắn")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_nav_chat) // đổi icon nếu cần
                .setOngoing(true)
                .build();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // Hàm gọi từ ViewModel hoặc Repository
    public static void start(Context context, Uri uri, String chatId, String messageId) {
        Intent intent = new Intent(context, SendMediaMessageService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_CHAT_ID, chatId);
        intent.putExtra(EXTRA_MESSAGE_ID, messageId);
        Log.d("SendMediaMessageService", "start:");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
