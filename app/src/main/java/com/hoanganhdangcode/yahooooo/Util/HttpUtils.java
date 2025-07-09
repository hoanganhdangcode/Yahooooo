package com.hoanganhdangcode.yahooooo.Util;

import okhttp3.*;
import java.io.IOException;

public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient();

    public static void postJson(String url, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
    public static void postJsonWithToken(String url, String jsonBody, String token, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(callback);
    }
    public static void getJsonWithToken(String url, String token, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(callback);
    }


}

