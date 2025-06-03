package com.hoanganhdangcode.yahooooo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.AuthViewModel;

public class CheckPass extends AppCompatActivity {
    EditText editTextPassword;
    Button buttonConfirm, buttonCancel;
    ProgressBar progressBar;
    AuthViewModel authViewModel;
    String currentUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.check_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            currentUid = Utils.getpref(CheckPass.this, "logined", "uid");
//            work = getIntent().getStringExtra("work");
        }
        catch (Exception e) {
           Log.e("Error", e.getMessage());
        }
        if (currentUid == null) {
            Intent intent = new Intent(CheckPass.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
//        if (work == null) {
//            finish();
//        }

        editTextPassword = findViewById(R.id.epassword);
        buttonConfirm = findViewById(R.id.btnxacnhan);
        buttonCancel = findViewById(R.id.btncancel);
        progressBar = findViewById(R.id.progressbar);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getIsLoading().observe(this, isLoading -> {
           progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
           buttonConfirm.setEnabled(!isLoading? true : false);
        });
        authViewModel.getCheckSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                // Handle successful password check

                 Intent intent = new Intent(CheckPass.this, ChangePass.class);
                    intent.putExtra("uid", currentUid);
                    startActivity(intent);
                    finish();
                }

        });
        authViewModel.getCheckMessage().observe(this, message -> {
           Utils.noti(CheckPass.this, message);
            // Handle password check error
        });
        buttonConfirm.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString();
            if (password.isEmpty()) {
                Utils.noti(CheckPass.this, "Vui lòng nhập mật khẩu");
                return;
            }
            authViewModel.checkpass(currentUid, password);
        });


    }
}