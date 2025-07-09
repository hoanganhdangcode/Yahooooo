package com.hoanganhdangcode.yahooooo.Repository;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.id;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.serverip;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.tokenchangepass;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.hoanganhdangcode.yahooooo.Model.UserData;
import com.hoanganhdangcode.yahooooo.Model.UserLogin;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.HttpUtils;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Ref;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// repository/AuthRepository.java
public class AuthRepository {

    private static final String TAG = "AuthRepository";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    public interface SignupCallback {
        void onSuccess(String uid);

        void onFailure(Exception e);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signup(String name, int gender, String birth, String phone, String password, SignupCallback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject json = new JSONObject();
            json.put("phone", phone);
            json.put("password", password);
            json.put("name", name);
            json.put("birth", birth);
            json.put("gender", gender);

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url("http://" + serverip + ":8080/auth/signup")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String resBody = response.body().string();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Đăng ký thành công: " + resBody);
                        callback.onSuccess(resBody);
                    } else {
                        Log.e(TAG, "Lỗi server: " + resBody);
                        callback.onFailure(new Exception("Lỗi server: " + resBody));
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Lỗi kết nối: " + e.getMessage());
                    callback.onFailure(new Exception("Lỗi kết nối: " + e.getMessage()));
                }

            });

        } catch (Exception e) {
            Log.e(TAG, "Lỗi tạo request: " + e.getMessage());
            callback.onFailure( e);
        }
    }
    public void signin(String phone, String pass, SigninCallback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            // Tạo JSON body
            JSONObject json = new JSONObject();
            json.put("phone", phone);
            json.put("password", pass);

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url("http://" + serverip + ":8080/auth/signin")
                    .header("User-Agent", "Android; "+Build.MODEL+"; "+Build.VERSION.RELEASE)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String resBody = response.body().string();

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(resBody);
                            long jid = jsonResponse.getLong("id");
                            String jaccessToken = jsonResponse.getString("accesstoken");
                            String jrefreshToken = jsonResponse.getString("refreshtoken");

                            // Truyền dữ liệu về callback
                            callback.onSuccess(jid, jaccessToken, jrefreshToken);

                        } catch (JSONException e) {
                            Log.e(TAG, "Lỗi parse JSON: " + e.getMessage());
                            callback.onFailure(e);
                        }
                    } else {
                        Log.e(TAG, "Lỗi server: " + resBody);
                        callback.onFailure(new Exception("Lỗi server: " + resBody));
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Lỗi kết nối: " + e.getMessage());
                    callback.onFailure(new Exception("Lỗi kết nối: " + e.getMessage()));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Lỗi tạo request: " + e.getMessage());
            callback.onFailure(e);
        }
    }


                public interface  SigninCallback{
                 void onSuccess(long id,String accesstoken, String refreshtoken);
                 void onFailure(Exception e);

                }

                public interface CheckPassCallback {
                    void onSuccess();
                    void onFailure(Exception e);
                }
                public void checkpass(String currentUid, String oldpass, CheckPassCallback callback) {
                    firestore.collection("userlogin")
                            .document(currentUid)
                            .get()
                            .addOnSuccessListener(document -> {
                                if (document.exists()) {
                                    UserLogin userLogin = document.toObject(UserLogin.class);
                                    String hashedPass = UtilsCrypto.md5(oldpass + userLogin.getSalt());
                                    if (hashedPass.equals(userLogin.getHashpass())) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure(new Exception("Mật khẩu không chính xác"));
                                    }
                                } else {
                                    callback.onFailure(new Exception("Tài khoản không tồn tại"));
                                }
                            })
                            .addOnFailureListener(e -> callback.onFailure(new Exception("Lỗi truy vấn")));
                }
                public interface UpdatePassCallback {
                    void onSuccess();
                    void onFailure(Exception e);
                }
                public void updatePass( String newPass, UpdatePassCallback callback) {
                    String json = "{\"id\":" + id + ",\"newpass\":\"" + newPass + "\",\"token\":\"" + tokenchangepass + "\"}";

                    HttpUtils.postJson("http://" + serverip + ":8080/auth/updatepassword", json, new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, "Cập nhật mật khẩu thất bại: " + e.getMessage());
                            callback.onFailure(new Exception("Cập nhật mật khẩu thất bại: " + e.getMessage()));
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "Cập nhật mật khẩu thành công");
                                callback.onSuccess();
                            } else {
                                Log.e(TAG, "Lỗi server: " + response.message());
                                callback.onFailure(new Exception("Lỗi server: " + response.message()));
                            }
                        }
                    });

//                    firestore.collection("userlogin")
//                            .document(currentUid)
//                            .get()
//                            .addOnSuccessListener(document -> {
//                                if (document.exists()) {
//                                    UserLogin userLogin = document.toObject(UserLogin.class);
//                                    String newSalt = null;
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        newSalt = Utils.genSalt();
//                                    }
//                                    String newHashedPass = UtilsCrypto.md5(newPass + newSalt);
//                                    userLogin.setHashpass(newHashedPass);
//                                    userLogin.setSalt(newSalt);
//
//                                    firestore.collection("userlogin")
//                                            .document(currentUid)
//                                            .set(userLogin)
//                                            .addOnSuccessListener(unused -> callback.onSuccess())
//                                            .addOnFailureListener(e -> callback.onFailure(new Exception("Cập nhật mật khẩu thất bại")));
//                                } else {
//                                    callback.onFailure(new Exception("Tài khoản không tồn tại"));
//                                }
//                            })
//                            .addOnFailureListener(e -> callback.onFailure(new Exception("Lỗi truy vấn")));
                }




}


