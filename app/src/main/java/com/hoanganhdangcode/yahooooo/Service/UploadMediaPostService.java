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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hoanganhdangcode.yahooooo.Model.MediaPost;
import com.hoanganhdangcode.yahooooo.Model.UserPost;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Repository.CloudinaryUploader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadMediaPostService extends Service {
    public static final String ACTION_UPLOAD_SUCCESS = "UPLOAD_MEDIA_SUCCESS";
    public static final String ACTION_UPLOAD_FAILURE = "UPLOAD_MEDIA_FAILURE";
    public static final String ACTION_UPLOAD_POST = "SEND_POST_MEDIA";
    public static final String EXTRA_POST_ID = "post_id";
    private static final String CHANNEL_ID = "send_post_channel";
    private static final String UID_POST = "uid_post";
    private static final String ERROR_POST = "error_post";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_UPLOAD_POST.equals(intent.getAction())) {
            startForeground(1, createNotification("Đăng bài viết"));

            String postId = intent.getStringExtra(EXTRA_POST_ID);
            String currentuid = intent.getStringExtra(UID_POST);
            DatabaseReference postRef = FirebaseDatabase.getInstance()
                    .getReference("userposts")
                    .child(currentuid).child(postId);

            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserPost post = snapshot.getValue(UserPost.class);
                        List<MediaPost> postList = post.getPostlist();
                        AtomicInteger uploadedCount = new AtomicInteger(0);
                        int total = postList.size();

                        for (MediaPost mdp : postList) {
                            if (mdp.getStatus() != 1 || mdp.getUrl().isEmpty()) {
                                CloudinaryUploader.uploadAuto(getApplicationContext(), Uri.parse(mdp.getLocalUri()), new CloudinaryUploader.UploadCallback() {
                                    @Override
                                    public void onSuccess(String url) {
                                        mdp.setUrl(url);
                                        mdp.setStatus(1);
                                        checkAllUploaded();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        mdp.setStatus(-1);
                                        postRef.child("status").setValue(-1);
                                        sendFailureBroadcast(postId, error);
                                        stopForeground(true);
                                        stopSelf();
                                    }

                                    private void checkAllUploaded() {
                                        int done = uploadedCount.incrementAndGet();
                                        if (done == total && isAllUploaded(postList)) {
                                            post.setPostlist(postList);
                                            post.setStatus(1);
                                            postRef.setValue(post);

                                            sendSuccessBroadcast(postId);
                                            stopForeground(true);
                                            stopSelf();
                                        }
                                    }
                                });
                            } else {
                                uploadedCount.incrementAndGet();
                            }
                        }
                    } else {
                        Log.d("Firebase", "Post not found");
                        sendFailureBroadcast(postId, "Post not found");
                        stopForeground(true);
                        stopSelf();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sendFailureBroadcast(postId, String.valueOf(error.getCode()));
                    stopForeground(true);
                    stopSelf();
                }
            });
        }
        return START_NOT_STICKY;
    }

    private boolean isAllUploaded(List<MediaPost> list) {
        for (MediaPost m1 : list) {
            if (m1.getUrl().isEmpty() || m1.getStatus() != 1) return false;
        }
        return true;
    }

    private void sendSuccessBroadcast(String postId) {
        Intent successIntent = new Intent(ACTION_UPLOAD_SUCCESS);
        successIntent.putExtra(EXTRA_POST_ID, postId);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(successIntent);
    }

    private void sendFailureBroadcast(String postId, String error) {
        Intent failureIntent = new Intent(ACTION_UPLOAD_FAILURE);
        failureIntent.putExtra(EXTRA_POST_ID, postId);
        failureIntent.putExtra(ERROR_POST, error);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(failureIntent);
    }

    private Notification createNotification(String contentText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Đăng bài viết", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Đang đăng bài viết")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_nav_feed)
                .setOngoing(true)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context, String postId, String uid) {
        Intent intent = new Intent(context, UploadMediaPostService.class);
        intent.setAction(ACTION_UPLOAD_POST);
        intent.putExtra(EXTRA_POST_ID, postId);
        intent.putExtra(UID_POST, uid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}