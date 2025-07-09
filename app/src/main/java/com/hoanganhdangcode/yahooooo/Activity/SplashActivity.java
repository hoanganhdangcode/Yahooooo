package com.hoanganhdangcode.yahooooo.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.Utils;

public class SplashActivity extends AppCompatActivity {
//Handler h;
final static String TAG = "SplashActivity";
     Class<?> nextActivity = SigninActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("nightmode", MODE_PRIVATE);
        Boolean nightmode = sharedPreferences.getBoolean("nightmode", false);
        if (nightmode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else{ AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        ImageView imageView = findViewById(R.id.icon);
        if (Utils.tontaipref(SplashActivity.this,"logined")){
            SharedPreferences sharedPreferences1 = getSharedPreferences("logined", MODE_PRIVATE);
        AppMng.id = sharedPreferences1.getLong("id", -1);
        AppMng.accesstoken = sharedPreferences1.getString("accesstoken", "");
        AppMng.refreshtoken = sharedPreferences1.getString("refreshtoken", "");
        if (AppMng.id != -1 && !AppMng.accesstoken.isEmpty() && !AppMng.refreshtoken.isEmpty()) {
            if (AppMng.tokensaphethan()){
                AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                    @Override
                    public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                        sharedPreferences1.edit().putString("accesstoken", accessToken).apply();
                        Log.d("SplashActivity", "Access token refreshed successfully"+accessToken);
                        nextActivity = HomeActivity.class;
                        Intent intent = new Intent(SplashActivity.this, nextActivity);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onRefreshFailure() {
                        Utils.xoapref(SplashActivity.this, "logined");
                        nextActivity = SigninActivity.class;
                        Intent intent = new Intent(SplashActivity.this, nextActivity);
                        Log.d("SplashActivity", "Access token refresh failed");
                        startActivity(intent);
                        finish();
                    }
                });

            }
            else {
                nextActivity = HomeActivity.class;
                Intent intent = new Intent(SplashActivity.this, nextActivity);
                Log.d(TAG, "onCreate: Da dang nhap");
                startActivity(intent);
                finish();
            }
        }
        else {
            Utils.xoapref(this,"logined");
            nextActivity = SigninActivity.class;
            Intent intent = new Intent(SplashActivity.this, nextActivity);
            Log.d(TAG, "onCreate: Chua dang nhap");
            startActivity(intent);
            finish();
        }
        }
        else{
            nextActivity = SigninActivity.class;
            Intent intent = new Intent(SplashActivity.this, nextActivity);
            Log.d(TAG, "onCreate: Chua dang nhap");
            startActivity(intent);
            finish();}







            Glide.with(this)
                .asGif()
                .load(R.drawable.splashicon) // Nếu GIF trong res/drawable
                // .load("https://example.com/yourgif.gif") // Nếu GIF từ URL
                .into(imageView);



//       h= new Handler();
//        Runnable runnable = () -> {
//            nextScreen();
//        };
//        h.postDelayed(runnable, 7000);
//        imageView.setOnClickListener(v -> {
//            nextScreen();
//            h.removeCallbacksAndMessages(null);
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (h!= null) {
//            h.removeCallbacksAndMessages(null);
//        }
    }
    public void nextScreen(){
        Intent intent = new Intent(SplashActivity.this, nextActivity);
        startActivity(intent);
        finish();
    }

}