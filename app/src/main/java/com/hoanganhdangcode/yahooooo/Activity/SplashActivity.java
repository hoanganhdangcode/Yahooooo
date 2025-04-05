package com.hoanganhdangcode.yahooooo.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;

public class SplashActivity extends AppCompatActivity {
Handler h;
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
        } else{                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        ImageView imageView = findViewById(R.id.icon);


        Glide.with(this)
                .asGif()
                .load(R.drawable.splashicon) // Nếu GIF trong res/drawable
                // .load("https://example.com/yourgif.gif") // Nếu GIF từ URL
                .into(imageView);



       h= new Handler();
        Runnable runnable = () -> {
            nextScreen();
        };

        h.postDelayed(runnable, 7000);
        imageView.setOnClickListener(v -> {
            nextScreen();
            h.removeCallbacksAndMessages(null);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (h!= null) {
            h.removeCallbacksAndMessages(null);
        }
    }
    public void nextScreen(){
        if (Utils.tontaipref(SplashActivity.this,"logined")){

            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

            startActivity(intent);
            if (h!= null) {
                h.removeCallbacksAndMessages(null);
            }
            finish(); // Đóng SplashActivity
        }else{
            Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
            startActivity(intent);
            if (h!= null) {
                h.removeCallbacksAndMessages(null);
            }
            finish(); }

    }

}