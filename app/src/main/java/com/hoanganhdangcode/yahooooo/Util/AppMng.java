package com.hoanganhdangcode.yahooooo.Util;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppMng {
    public final static String serverip = "172.16.243.217" ;
    public volatile static long id =-1;
    public volatile static String accesstoken="";
    public volatile static String refreshtoken="";
    public static String otpid;
    public static String tokenchangepass= "";


    public static boolean tokensaphethan(){
        try {
            // Lấy phần payload từ JWT
            String[] parts = accesstoken.split("\\.");
            if (parts.length != 3) return true;

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject payload = new JSONObject(payloadJson);

            long exp = payload.getLong("exp");
            long now = System.currentTimeMillis() / 1000;

            return exp < now+ 60; // true = hết hạn
        } catch (Exception e) {
            e.printStackTrace();
            return true; // lỗi => coi như hết hạn
        }

    }
    public static boolean tokenhethan() {
        try {
            // Lấy phần payload từ JWT
            String[] parts = accesstoken.split("\\.");
            if (parts.length != 3) return true;

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject payload = new JSONObject(payloadJson);

            long exp = payload.getLong("exp");
            long now = System.currentTimeMillis() / 1000;

            return exp < now; // true = hết hạn
        } catch (Exception e) {
            e.printStackTrace();
            return true; // lỗi => coi như hết hạn
        }
    }
    public interface RefreshCallback {
        void onRefreshSuccess(long  id,String accessToken, String refreshToken);
        void onRefreshFailure();
    }
    public static void refreshtokendata( RefreshCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("http://"+serverip+":8080/refreshtoken"); // ví dụ API refresh
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("PATCH"); // hoặc POST tùy BE
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setDoOutput(true);

                // JSON body
                JSONObject json = new JSONObject();
                json.put("id", id);
                json.put("accesstoken", accesstoken);
                json.put("refreshtoken", refreshtoken);

                String jsonString = json.toString();
                OutputStream os = con.getOutputStream();
                os.write(jsonString.getBytes("UTF-8"));
                os.close();

                int status = con.getResponseCode();
                InputStream is = (status >= 200 && status < 300) ? con.getInputStream() : con.getErrorStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                Log.d("API_RESPONSE", response.toString());
                Log.d("API_STATUS", String.valueOf(status));

                if (status == 200) {
                    JSONObject resJson = new JSONObject(response.toString());
                    accesstoken = resJson.getString("accesstoken");
                    callback.onRefreshSuccess(id,accesstoken,refreshtoken);
                }
                else{
                    String error = "Error refreshing token: " + status;
                    Log.e("API_ERROR", error);
                    callback.onRefreshFailure();
                }
            } catch (Exception e) {
                Log.e("API_ERROR", e.toString());
            }
        }).start();
    }

}
