package com.hoanganhdangcode.yahooooo.Util;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Utils {

    public static final String urlavatardefault = "https://res.cloudinary.com/dbeomwlon/image/upload/v1744406492/avatardefault_xbaqul.png";
    public static final String urlbackgroundefault= "https://res.cloudinary.com/dbeomwlon/image/upload/v1744406492/backgroun%C4%91efaulr_w0pcvc.jpg";
    public static final String urlavatarerror = "https://static.vecteezy.com/system/resources/previews/034/998/724/non_2x/corrupted-pixel-file-icon-damage-document-symbol-sign-broken-data-vector.jpg";

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
public static String getpref(Context context, String pref, String field){
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
    public static boolean regexbirth(String s) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/[0-9]{4}$";
        return s.matches(regex);
    }
    public static boolean regexpassword(String s) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return s.matches(regex);
    }
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
    public static boolean isValidPass(String s){
    return s.length()>=6;
    }
    public static boolean isValidPhone(String countryISO, String phone) {
        switch (countryISO) {
            case "VN": // Việt Nam
                return phone.matches("^(03|05|07|08|09)[0-9]{8}$");
            case "US": // Mỹ
                return phone.matches("^[2-9]{1}[0-9]{9}$"); // 10 số, không bắt đầu bằng 0/1
            case "IN": // Ấn Độ
                return phone.matches("^[6-9]\\d{9}$");
            default:
                return Patterns.PHONE.matcher(phone).matches(); // fallback
        }
    }
    public static boolean isValidBirth(String birth){

    return birth.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d\\d)$");
    }
    public static String formatTime(long millis){
    Calendar messageCal = Calendar.getInstance();
        messageCal.setTimeInMillis(millis);

        Calendar today = Calendar.getInstance();

        boolean isToday = messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
        boolean isYesterday = messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1;
        boolean inyear = messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR);

        SimpleDateFormat sdf;
        if(isToday)
        {  sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());}
        else if (isYesterday)
        {  sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(new Date(millis)) + " Hôm qua";

        }
        else if (inyear)
        {  sdf = new SimpleDateFormat(" HH:mm dd/MM ", Locale.getDefault());}
        else
        {sdf = new SimpleDateFormat(" HH:mm dd/MM/yyyy ", Locale.getDefault());}

        return sdf.format(new Date(millis));
    }



}

