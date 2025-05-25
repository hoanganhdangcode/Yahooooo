package com.hoanganhdangcode.yahooooo.Repository;

import android.content.Context;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudinaryUploader {

    private static final String CLOUD_NAME = "dbeomwlon";
    private static final String UPLOAD_PRESET = "android_upload";

    public interface UploadCallback {
        void onSuccess(String url);
        void onFailure(String error);
    }

    public static void uploadAuto(Context context, Uri uri, UploadCallback callback) {
        String type = context.getContentResolver().getType(uri);
        if (type == null) {
            callback.onFailure("Không xác định được loại file.");
            return;
        }
        if (type.startsWith("image/")) {
            uploadImage(context, uri, callback);
        } else if (type.startsWith("video/")) {
            uploadVideo(context, uri, callback);
        } else {
            callback.onFailure("Chỉ hỗ trợ ảnh hoặc video.");
        }
    }

    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);

            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType == null) mimeType = "image/jpeg";

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg",
                            RequestBody.create(imageBytes, MediaType.parse(mimeType)))
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            String imageUrl = json.getString("secure_url");
                            callback.onSuccess(imageUrl);
                        } catch (JSONException e) {
                            callback.onFailure("JSON parse error: " + e.getMessage());
                        }
                    } else {
                        callback.onFailure("Upload ảnh thất bại: " + response.message());
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure("Lỗi đọc ảnh: " + e.getMessage());
        }
    }


    public static void uploadVideo(Context context, Uri videoUri, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(videoUri);
            byte[] videoBytes = getBytes(inputStream);

            String mimeType = context.getContentResolver().getType(videoUri);
            if (mimeType == null) mimeType = "video/mp4";

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "video.mp4",
                            RequestBody.create(videoBytes, MediaType.parse(mimeType)))
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/video/upload")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            String videoUrl = json.getString("secure_url");
                            callback.onSuccess(videoUrl);
                        } catch (JSONException e) {
                            callback.onFailure("JSON parse error: " + e.getMessage());
                        }
                    } else {
                        callback.onFailure("Upload video thất bại: " + response.message());
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure("Lỗi đọc video: " + e.getMessage());
        }
    }


    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
