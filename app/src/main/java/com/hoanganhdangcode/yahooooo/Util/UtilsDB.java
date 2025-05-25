package com.hoanganhdangcode.yahooooo.Util;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.hoanganhdangcode.yahooooo.Model.UserFriend;
import com.hoanganhdangcode.yahooooo.Model.UserLogin;

import java.util.ArrayList;
import java.util.List;
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
    public static void check( String collection, String phone, CheckCallback callback){
        db.collection(collection).whereEqualTo("phone",phone)
    .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String uid = queryDocumentSnapshots.getDocuments().get(0).getId();
                        callback.onSuccess(uid);
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
 public static void getstatusfriend(String thisuid, String uid, GetstatusCallback callback){
        db.collection("userfriend").document(thisuid).collection("friend").document(uid).
                get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onExist(documentSnapshot.getLong("status").intValue());
                    }else{
                        if (thisuid.equals(uid)){
                            callback.onNotExist(0);
                        }
                        else{
                            callback.onNotExist(-1);
                        }
                    }
                })
                .addOnFailureListener(
                        e -> {
                            callback.onFailure(e);
                        }

                );
 }
        public interface GetstatusCallback {
        public void onExist(int status);
        public  void onNotExist(int status);
        public void onFailure(Exception e);
        }
 public static void changestatusfriend(String thisuid, String uid,int stt1, int stt2, ChangeStatusFriendCallback callback){
     WriteBatch batch = db.batch();
     if (stt1>0){
         UserFriend userFriend1 = new UserFriend(thisuid,uid,stt1,Utils.gettime());
         UserFriend userFriend2 = new UserFriend(uid,thisuid,stt2,Utils.gettime());
         DocumentReference ref1 = db.collection("userfriend")
                 .document(thisuid)
                 .collection("friend")
                 .document(uid);

         DocumentReference ref2 = db.collection("userfriend")
                 .document(uid)
                 .collection("friend")
                 .document(thisuid);
         batch.set(ref1, userFriend1);
         batch.set(ref2, userFriend2);
     }
     else {
         DocumentReference ref1 = db.collection("userfriend")
                 .document(thisuid)
                 .collection("friend")
                 .document(uid);

         DocumentReference ref2 = db.collection("userfriend")
                 .document(uid)
                 .collection("friend")
                 .document(thisuid);

         batch.delete(ref1);
         batch.delete(ref2);
     }
     batch.commit()
             .addOnSuccessListener(unused -> {
                 if (callback != null) callback.onSuccess();
             })
             .addOnFailureListener(e -> {
                 if (callback != null) callback.onFailure(e);
             });

 }
        public interface ChangeStatusFriendCallback {
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
                            callback.onNotExist();
                        }

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
    public interface SearchCallback<T> {
        void onResult(List<T> result);
        void onFailure(Exception e);
    }

    public static <T> void searchByFieldContains(
            String collection,
            String field,
            String keyword,
            Class<T> clazz,
            SearchCallback<T> callback
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<T> resultList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String value = doc.getString(field);
                        if (value != null && value.toLowerCase().contains(keyword.toLowerCase())) {
                            resultList.add(doc.toObject(clazz));
                        }
                    }
                    callback.onResult(resultList);
                })
                .addOnFailureListener(callback::onFailure);
    }

}
