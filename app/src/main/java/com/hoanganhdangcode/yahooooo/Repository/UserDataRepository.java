package com.hoanganhdangcode.yahooooo.Repository;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoanganhdangcode.yahooooo.Model.UserData;

public class UserDataRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnUserDataLoaded {
        void onSuccess(UserData userData);
        void onFailure(Exception e);
    }

    public void getUserDataById(String uid, OnUserDataLoaded callback) {
        db.collection("userdata").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserData userData = documentSnapshot.toObject(UserData.class);
                        callback.onSuccess(userData);
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy người dùng"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
