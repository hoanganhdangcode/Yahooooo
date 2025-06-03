package com.hoanganhdangcode.yahooooo.ViewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hoanganhdangcode.yahooooo.Model.UserPost;
import com.hoanganhdangcode.yahooooo.Repository.FeedRepository;

import java.util.List;

public class FeedViewModel extends AndroidViewModel {

    private final FeedRepository repository;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        repository = new FeedRepository(application);
    }
    public LiveData<List<UserPost>> getPostList() {
        return repository.getPostListLiveData();
    }

    public LiveData<String> getUploadSuccessLiveData() {
        return repository.getUploadSuccessLiveData();
    }

    public LiveData<String> getUploadFailureLiveData() {
        return repository.getUploadFailureLiveData();
    }

    public void createPost(UserPost post, Context context) {
        repository.createPost(post,context);
    }

    public void loadNearbyPosts(String currentUid) {
        repository.loadNearbyPosts(currentUid);
    }

    public void sortByTime(boolean newestFirst) {
        repository.sortPostsByTime(newestFirst);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.unregisterReceiver(getApplication());
    }
}
