package com.hoanganhdangcode.yahooooo.Util;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.UUID;

public class Utils {


public static void noti(Context context, String s){
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
}
public static String genuuid(){
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
            return String.valueOf(year) + String.valueOf(month) + String.valueOf(day) + UUID.randomUUID().toString();
}
    public static void showpass(EditText e){
        e.setTransformationMethod(null);
        e.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (hasFocus) {
                        e.setTransformationMethod(null);
                    } else {
                        new Handler().postDelayed(() -> {
                            e.setTransformationMethod(new PasswordTransformationMethod());
                        },1000);
                    }}
        );

    }
    public static void hiddenpass(EditText e){
        e.setTransformationMethod(new PasswordTransformationMethod());
    }
public static String getpref(Context context,String pref, String field){
    SharedPreferences sharedPreferences = context.getSharedPreferences(pref, MODE_PRIVATE);
    return sharedPreferences.getString(field, "null");
}

    public static void xoapref(Context context, String pref){
            SharedPreferences sharedPreferences = context.getSharedPreferences(pref, MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
    }
    public static boolean tontaipref(Context context, String pref){
        SharedPreferences prefs = context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        return  !prefs.getAll().isEmpty();
    }
    public static void savepref(Context context,String pref, String field,String uid){
        SharedPreferences preferences = context.getSharedPreferences(pref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uid", uid);

// Lưu lại
        editor.apply();
    }

    public static String genOtp(){
    SecureRandom random = new SecureRandom();
        String otp = "";
        for (int i = 0; i < 6; i++) {
            otp += String.valueOf(random.nextInt(10));
        }
        return otp;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String genSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[random.nextInt(10)];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    public static boolean regexphone(String s) {
        String regex = "^(03|05|07|08|09)[0-9]{8}$";
        return s.matches(regex);}
    public static long gettime(){
        long t = System.currentTimeMillis();
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                t = Instant.now().toEpochMilli();
            }
        }
        catch (Exception e){
            Utils.noti(null, "Lỗi thời gian");
            return t;
        }
        return t;
    }








}

