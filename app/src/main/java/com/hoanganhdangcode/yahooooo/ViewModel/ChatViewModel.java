package com.hoanganhdangcode.yahooooo.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.hoanganhdangcode.yahooooo.Model.ChatDisplay;
import com.hoanganhdangcode.yahooooo.Repository.ChatRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository repository = new ChatRepository();

    private final MutableLiveData<List<ChatDisplay>> fullChatList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ChatDisplay>> filteredChatList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private String currentUid;
    private String keyword = "";
    private String filter = "ALL"; // ALL, FRIEND, GROUP, WAITING

    private ListenerRegistration chatboxListener;

    public LiveData<List<ChatDisplay>> getChatDisplayLiveData() {
        return filteredChatList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setCurrentUid(String uid) {
        this.currentUid = uid;
        loadChatDisplay();
        listenToChatboxUpdates();
    }

    public void setSearchKeyword(String keyword) {
        this.keyword = keyword.toLowerCase();
        applyFilterAndSearch();
    }

    public void setFilter(String filter) {
        this.filter = filter;
        applyFilterAndSearch();
    }

    public void loadChatDisplay() {
        if (currentUid == null || currentUid.isEmpty()) return;
        isLoading.setValue(true);

        repository.loadChat(currentUid, new ChatRepository.loadChatCallback() {
            @Override
            public void onComplete(List<ChatDisplay> list) {
                Collections.sort(list, (a, b) -> Long.compare(b.getLasttimestamp(), a.getLasttimestamp()));
                fullChatList.setValue(list);
                isLoading.setValue(false);
                applyFilterAndSearch();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ChatViewModel", "Lỗi khi load danh sách chat", e);
                fullChatList.setValue(new ArrayList<>());
                filteredChatList.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    private void applyFilterAndSearch() {
        List<ChatDisplay> baseList = fullChatList.getValue();
        if (baseList == null) return;
        List<ChatDisplay> filtered = new ArrayList<>();
        for (ChatDisplay chat : baseList) {
            boolean typeMatch=false;
            switch (filter) {
                case "FRIEND":
                    typeMatch = (chat.getType() == 1);
                    break;
                case "GROUP":
                    typeMatch = (chat.getType() == 2);
                    break;
                case "WAITING":
                    typeMatch = (chat.getType() <= 0);
                    break;
                case "ALL":
                    typeMatch = true;
                    break;
            }
            boolean keywordMatch = keyword.isEmpty()
                    ||( (chat.getName() != null && chat.getName().toLowerCase().contains(keyword)));

            if (typeMatch && keywordMatch) {
                filtered.add(chat);
            }
        }
        filteredChatList.setValue(filtered);
    }

    private void listenToChatboxUpdates() {
        if (currentUid == null || currentUid.isEmpty()) return;

        if (chatboxListener != null) chatboxListener.remove();

        chatboxListener = FirebaseFirestore.getInstance()
                .collection("chatbox")
                .addSnapshotListener((snapshots, e) -> {
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
                        loadChatDisplay();
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (chatboxListener != null) chatboxListener.remove();
    }
}
