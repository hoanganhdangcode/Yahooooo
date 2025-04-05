package com.hoanganhdangcode.yahooooo.Util;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UtilsDB {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void create(String collection, String documentId, Object object, CreateCallback callback) {
        db.collection(collection).document(documentId)
                .set(object)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }
    public interface CreateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    public static void check( String collection, String documentIdcheck, CheckCallback callback){
        db.collection(collection).document(documentIdcheck).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userid = documentSnapshot.getString("uid");
                        callback.onSuccess(userid);
                    }
                    else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
    public interface CheckCallback {
        public void onSuccess(String userid);
        public void onFailure(Exception e);

    }

    public static <T> void read(String collection, String documentId ,Class <T> classt, ReadCallback<T> callback){
        db.collection(collection).document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {


                            callback.onSuccess(documentSnapshot.toObject(classt));


                    }
                    else {
                        callback.onNotFound();
                        }
                    })
                .addOnFailureListener(
                        e -> {
                            callback.onFailure(e);
                        }
                );

    }

        public interface ReadCallback<T> {
            public void onSuccess(T result);
           public void onNotFound();
           public void onFailure(Exception e);
        }
    public static void update(String collection, String docId, Map<String, Object> updates, UpdateCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection).document(docId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public interface UpdateCallback {
       public void onSuccess();
       public void onFailure(Exception e);
    }
        public static void getuid( String documentId, GetuidCallback callback){
            db.collection("userlogin").document(documentId)
                    .get()
                    .addOnSuccessListener(documenSnapshot -> {
                        if (documenSnapshot.exists()) {
                        callback.onExist(documenSnapshot.getString("uid"));
                    }else{
                        }
                        callback.onNotExist();
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                callback.onFailure(e);
                            }
                    );
        }
        public interface GetuidCallback {
        public void onExist(String uid);
        public  void onNotExist();
        public void onFailure(Exception e);
        }

}
