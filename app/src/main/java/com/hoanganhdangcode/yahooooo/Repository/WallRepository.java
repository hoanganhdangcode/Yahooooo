package com.hoanganhdangcode.yahooooo.Repository;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.hoanganhdangcode.yahooooo.Model.UserData;
import android.util.Log;

public class WallRepository {
    private static final String TAG = "WallRepository";
    private ListenerRegistration userListener;
    private ListenerRegistration friendListener;

    public interface LoadCallback {
        void onSuccess(UserData userData);
        void onFailure(Exception e);
        void status(int status);
    }

    // Load data một lần (không lắng nghe thay đổi)
    public void load(String uid, String currentUid, LoadCallback callback) {
        // Validate input parameters
        if (uid == null || uid.trim().isEmpty()) {
            callback.onFailure(new Exception("UID cannot be null or empty"));
            return;
        }

        if (currentUid == null || currentUid.trim().isEmpty()) {
            callback.onFailure(new Exception("Current UID cannot be null or empty"));
            return;
        }

        // Load user data
        FirebaseFirestore.getInstance()
                .collection("user")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (documentSnapshot.exists()) {
                            UserData userData = documentSnapshot.toObject(UserData.class);
                            if (userData != null) {
                                callback.onSuccess(userData);
                            } else {
                                callback.onFailure(new Exception("Failed to parse user data"));
                            }
                        } else {
                            callback.onFailure(new Exception("User does not exist"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing user data", e);
                        callback.onFailure(e);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load user data", e);
                    callback.onFailure(e);
                });

        // Check friendship status
        checkFriendshipStatus(uid, currentUid, callback);
    }

    // Load data với lắng nghe thay đổi
    public void loadWithListener(String uid, String currentUid, LoadCallback callback) {
        // Validate input parameters
        if (uid == null || uid.trim().isEmpty()) {
            callback.onFailure(new Exception("UID cannot be null or empty"));
            return;
        }

        if (currentUid == null || currentUid.trim().isEmpty()) {
            callback.onFailure(new Exception("Current UID cannot be null or empty"));
            return;
        }

        // Listen to user data changes
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("user")
                .document(uid);

        userListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed for user data", e);
                callback.onFailure(e);
                return;
            }

            try {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    UserData userData = documentSnapshot.toObject(UserData.class);
                    if (userData != null) {
                        callback.onSuccess(userData);
                    } else {
                        callback.onFailure(new Exception("Failed to parse user data"));
                    }
                } else {
                    callback.onFailure(new Exception("User does not exist"));
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error processing user data", ex);
                callback.onFailure(ex);
            }
        });

        // Listen to friendship status changes
        listenToFriendshipStatus(uid, currentUid, callback);
    }

    private void checkFriendshipStatus(String uid, String currentUid, LoadCallback callback) {
        if (currentUid.equals(uid)) {
            callback.status(-2); // Same user
        } else {
            FirebaseFirestore.getInstance()
                    .collection("userfriend")
                    .document(currentUid)
                    .collection("friend")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        try {
                            if (documentSnapshot.exists()) {
                                Long statusLong = documentSnapshot.getLong("status");
                                int status = (statusLong != null) ? statusLong.intValue() : -1;
                                callback.status(status);
                            } else {
                                callback.status(-1); // No friendship record found
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing friendship status", e);
                            callback.onFailure(e);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to check friendship status", e);
                        callback.onFailure(e);
                    });
        }
    }

    private void listenToFriendshipStatus(String uid, String currentUid, LoadCallback callback) {
        if (currentUid.equals(uid)) {
            callback.status(-2);
        } else {
            DocumentReference friendRef = FirebaseFirestore.getInstance()
                    .collection("userfriend")
                    .document(currentUid)
                    .collection("friend")
                    .document(uid);

            friendListener = friendRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Log.e(TAG, "Listen failed for friendship status", e);
                    callback.onFailure(e);
                    return;
                }

                try {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Long statusLong = documentSnapshot.getLong("status");
                        int status = (statusLong != null) ? statusLong.intValue() : -1;
                        callback.status(status);
                    } else {
                        callback.status(-1); // No friendship record found
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error processing friendship status", ex);
                    callback.onFailure(ex);
                }
            });
        }
    }

    // Dừng lắng nghe để tránh memory leak
    public void removeListeners() {
        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }

        if (friendListener != null) {
            friendListener.remove();
            friendListener = null;
        }
    }

    // Kiểm tra xem có đang lắng nghe không
    public boolean isListening() {
        return userListener != null || friendListener != null;
    }
}