package com.hoanganhdangcode.yahooooo.Repository;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hoanganhdangcode.yahooooo.Model.FriendDisplay;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ChatRepository chatRepository = new ChatRepository();

    public interface OnFriendListLoaded {
        void onComplete(List<FriendDisplay> list);
        void onError(Exception e);
    }

    public interface OnStatusChangeCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void loadFriends(String currentUid, String keyword, String filterType, OnFriendListLoaded callback) {
        Map<String, Integer> statusMap = new HashMap<>();
        Map<String, Long> timestampMap = new HashMap<>();

        db.collection("userfriend").document(currentUid).collection("friend")
                .get()
                .addOnSuccessListener(friendSnapshots -> {
                    for (DocumentSnapshot doc : friendSnapshots) {
                        String friendId = doc.getId();
                        int status = doc.getLong("status") != null ? doc.getLong("status").intValue() : -1;
                        long timestamp = doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0;
                        statusMap.put(friendId, status);
                        timestampMap.put(friendId, timestamp);
                    }

                    db.collection("user")
                            .limit(100)
                            .get()
                            .addOnSuccessListener(userSnapshots -> {
                                List<FriendDisplay> resultList = new ArrayList<>();

                                for (DocumentSnapshot doc : userSnapshots) {
                                    String uid = doc.getId();
                                    if (uid.equals(currentUid)) continue;
                                    String name = doc.getString("name") != null ? doc.getString("name") : "";
                                    String phone = doc.getString("phone") != null ? doc.getString("phone") : "";
                                    String avatar = doc.getString("avatar");

                                    if (!(name.toLowerCase().contains(keyword.toLowerCase()) || phone.contains(keyword))) continue;

                                    int status = statusMap.containsKey(uid) ? statusMap.get(uid) : -1;
                                    long timestamp = timestampMap.containsKey(uid) ? timestampMap.get(uid) : 0;

                                    boolean match = false;
                                    switch (filterType) {
                                        case "ALL":
                                            match = true;
                                            break;
                                        case "FRIENDS":
                                            match = (status == 5);
                                            break;
                                        case "INVITES":
                                            match = (status == 3 || status == 4);
                                            break;
                                        case "BLOCKED":
                                            match = (status == 1 || status == 2);
                                            break;
                                    }

                                    if (filterType.equals("ALL") || match) {
                                        resultList.add(new FriendDisplay(uid, name, phone, avatar, status, timestamp));
                                    }
                                }

                                Collections.sort(resultList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                                callback.onComplete(resultList);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    public void changeFriendStatus(String myUid, String targetUid, int statusMe, int statusThem, @Nullable OnStatusChangeCallback callback) {
        long timestamp = Utils.gettime();

        Map<String, Object> myMap = new HashMap<>();
        myMap.put("status", statusMe);
        myMap.put("timestamp", timestamp);

        Map<String, Object> theirMap = new HashMap<>();
        theirMap.put("status", statusThem);
        theirMap.put("timestamp", timestamp);

        FirebaseFirestore.getInstance().runBatch(batch -> {
            batch.set(db.collection("userfriend").document(myUid).collection("friend").document(targetUid), myMap, SetOptions.merge());
            batch.set(db.collection("userfriend").document(targetUid).collection("friend").document(myUid), theirMap, SetOptions.merge());
        }).addOnSuccessListener(unused -> {

            chatRepository.createPrivateChatIfNotExists(myUid, targetUid, gettype(statusMe),null);

                if (callback != null) callback.onSuccess();
        }).addOnFailureListener(e -> {
            if (callback != null) callback.onFailure(e);
        });
    }
    private int gettype(int i) {
        if (i == 1 || i == 2) return -1;
        if (i == 5) return 1;
        return 0;
    }

}

