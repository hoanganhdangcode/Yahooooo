package com.hoanganhdangcode.yahooooo.Repository;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.hoanganhdangcode.yahooooo.Model.UserData;
import com.hoanganhdangcode.yahooooo.Model.UserLogin;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;

import java.sql.Ref;

// repository/AuthRepository.java
public class AuthRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public interface SignupCallback {
        void onSuccess(String uid);

        void onFailure(Exception e);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signup(String name, int gender, String birth, String phone, String password, SignupCallback callback) {
        String uid = Utils.genuuid();
        String salt = Utils.genSalt();
        String hashedPass = UtilsCrypto.md5(password+salt);
        firestore.collection("userlogin")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        callback.onFailure( new Exception("Số điện thoại đã được đăng ký"));
                    } else {

        WriteBatch batch = firestore.batch();
        UserLogin login = new UserLogin( uid,phone,hashedPass,salt);
        UserData userData = new UserData( uid,  name,  phone, gender,  birth,  Utils.urlavatardefault,  Utils.urlbackgroundefault,  "",  0,  Utils.gettime(),  1);
        DocumentReference ref1 = firestore.collection("userlogin").document(uid);
        DocumentReference ref2 = firestore.collection("user").document(uid);
        batch.set(ref1, login);
        batch.set(ref2, userData);
        batch.commit()
                .addOnSuccessListener(unused -> {
                    callback.onSuccess(uid );
                })
                .addOnFailureListener(e -> {
                    callback.onFailure( new Exception("Đăng ký thất bại"));
                });


                }}).addOnFailureListener(e->{
                        callback.onFailure(new Exception("Lỗi truy vấn"));}
                        );
    }
    public void signin(String phone, String pass, SigninCallback callback){
        firestore.collection("userlogin")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        UserLogin userLogin = query.getDocuments().get(0).toObject(UserLogin.class);
                        String hashedPass = UtilsCrypto.md5(pass+userLogin.getSalt());
                        if (hashedPass.equals(userLogin.getHashpass())){
                            callback.onSuccess(userLogin.getUid());
                        } else {
                            callback.onFailure(new Exception("Sai mật khẩu"));
                        }

                    } else {
                        callback.onFailure(new Exception("Tài khoản không tồn tại"));
                    }


    }).addOnFailureListener(
            e->{
                callback.onFailure(new Exception("Lỗi truy vấn"));}
                );
                }

                public interface  SigninCallback{
                 void onSuccess(String uid);
                 void onFailure(Exception e);

                }





    }


