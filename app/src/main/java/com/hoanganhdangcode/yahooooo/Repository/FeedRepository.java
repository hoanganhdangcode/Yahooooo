package com.hoanganhdangcode.yahooooo.Repository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hoanganhdangcode.yahooooo.Model.MediaPost;
import com.hoanganhdangcode.yahooooo.Model.UserPost;
import com.hoanganhdangcode.yahooooo.Service.UploadMediaPostService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FeedRepository {

    private final DatabaseReference realtimeDb = FirebaseDatabase.getInstance().getReference();

    private final MutableLiveData<List<UserPost>> postListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> uploadSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> uploadFailureLiveData = new MutableLiveData<>();

    private final BroadcastReceiver uploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            if (UploadMediaPostService.ACTION_UPLOAD_SUCCESS.equals(action)) {
                String uploadedUrl = intent.getStringExtra("uploaded_url");
                uploadSuccessLiveData.postValue(uploadedUrl);
            } else if (UploadMediaPostService.ACTION_UPLOAD_FAILURE.equals(action)) {
                String error = intent.getStringExtra("error_message");
                uploadFailureLiveData.postValue(error);
            }
        }
    };

    public FeedRepository(Context context) {
        registerUploadReceiver(context);
    }

    public LiveData<String> getUploadSuccessLiveData() {
        return uploadSuccessLiveData;
    }

    public LiveData<String> getUploadFailureLiveData() {
        return uploadFailureLiveData;
    }

    public LiveData<List<UserPost>> getPostListLiveData() {
        return postListLiveData;
    }

    public void createPost(UserPost post,Context context) {
        realtimeDb.child("posts")
                .child(post.getUid())
                .child(post.getPostid())
                .setValue(post);
        UploadMediaPostService.start(context, post.getPostid(),post.getUid());

    }

    public void loadNearbyPosts(String currentUid) {
        realtimeDb.child("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<UserPost> nearbyPosts = new ArrayList<>();
                        for (DataSnapshot postSnap : snapshot.getChildren()) {
                            UserPost post = postSnap.getValue(UserPost.class);
                            if (post != null && !post.getUid().equals(currentUid)) {
                                nearbyPosts.add(post);
                            }
                        }
                        postListLiveData.setValue(nearbyPosts);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    public void sortPostsByTime(boolean newestFirst) {
        List<UserPost> current = postListLiveData.getValue();
        if (current == null) return;
        current.sort((a, b) -> newestFirst
                ? Long.compare(b.getTimestamp(), a.getTimestamp())
                : Long.compare(a.getTimestamp(), b.getTimestamp()));
        postListLiveData.setValue(new ArrayList<>(current));
    }

    public void unregisterReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(uploadReceiver);
    }

    public void likePost(String postId, String uid) {
        DatabaseReference postRef = FirebaseDatabase.getInstance()
                .getReference("posts").child(uid)
                .child(postId);

        postRef.child("countlike").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentCount = 0;
                if (snapshot.exists()) {
                    currentCount = snapshot.getValue(Long.class);
                }
                postRef.child("countlike").setValue(currentCount + 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LikeError", "Failed to increment like: " + error.getMessage());
            }
        });

    }

    private void registerUploadReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UploadMediaPostService.ACTION_UPLOAD_SUCCESS);
        filter.addAction(UploadMediaPostService.ACTION_UPLOAD_FAILURE);
        LocalBroadcastManager.getInstance(context).registerReceiver(uploadReceiver, filter);
    }
}