package com.hoanganhdangcode.yahooooo.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.hoanganhdangcode.yahooooo.Model.FriendDisplay;
import com.hoanganhdangcode.yahooooo.Repository.ChatRepository;
import com.hoanganhdangcode.yahooooo.Repository.FriendRepository;

import java.util.List;

public class FriendViewModel extends ViewModel {
    private ListenerRegistration friendListener;

    private final FriendRepository repository = new FriendRepository();
    private final MutableLiveData<String> keywordLiveData = new MutableLiveData<>("");
    private final MutableLiveData<String> filterTypeLiveData = new MutableLiveData<>("ALL");

    private final MutableLiveData<List<FriendDisplay>> friendListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isActionLoadingLiveData = new MutableLiveData<>(false);

    private String currentUid;

    public LiveData<List<FriendDisplay>> getFriendListLiveData() {
        return friendListLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<Boolean> getIsActionLoadingLiveData() {
        return isActionLoadingLiveData;
    }

    public void setCurrentUid(String uid) {
        this.currentUid = uid;
        loadFriendList();
        listenFriendChange();
    }

    public void setKeyword(String keyword) {
        keywordLiveData.setValue(keyword);
        loadFriendList();
    }

    public void setFilterType(String filterType) {
        filterTypeLiveData.setValue(filterType);
        loadFriendList();
    }

    private void loadFriendList() {
        if (currentUid == null) return;

        isLoadingLiveData.setValue(true);

        String keyword = keywordLiveData.getValue() != null ? keywordLiveData.getValue() : "";
        String filterType = filterTypeLiveData.getValue() != null ? filterTypeLiveData.getValue() : "ALL";

        repository.loadFriends(currentUid, keyword, filterType, new FriendRepository.OnFriendListLoaded() {
            @Override
            public void onComplete(List<FriendDisplay> list) {
                friendListLiveData.postValue(list);
                isLoadingLiveData.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                Log.e("FriendViewModel", "Error loading friends: " + e.getMessage());
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void changeFriendStatus(String myUid, String targetUid, int statusMe, int statusThem) {
        isActionLoadingLiveData.setValue(true);
        repository.changeFriendStatus(myUid, targetUid, statusMe, statusThem, new FriendRepository.OnStatusChangeCallback() {
            @Override
            public void onSuccess() {
                isActionLoadingLiveData.postValue(false);
                loadFriendList();
            }

            @Override
            public void onFailure(Exception e) {
                isActionLoadingLiveData.postValue(false);
                Log.e("FriendViewModel", "Change status failed", e);
            }
        });
    }

    private void listenFriendChange() {
        if (currentUid == null || currentUid.isEmpty()) return;

        if (friendListener != null) friendListener.remove();

        friendListener = FirebaseFirestore.getInstance()
                .collection("userfriend").document(currentUid).collection("friend")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    boolean shouldRefresh = false;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                shouldRefresh = true;
                                break;
                            case REMOVED:
                                shouldRefresh = true;
                                break;
                            case MODIFIED:
                                shouldRefresh = true;
                                break;
                        }
                    }
                    if (shouldRefresh) {
                        loadFriendList();
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (friendListener != null) friendListener.remove();
    }

}
