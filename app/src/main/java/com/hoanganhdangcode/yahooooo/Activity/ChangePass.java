package com.hoanganhdangcode.yahooooo.Activity;

import android.os.Bundle;
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

public class ChangePass extends AppCompatActivity {
    EditText ePassword,econfirmPassword;
    Button buttonConfirm, buttonCancel;
    ProgressBar progressBar;
    AuthViewModel authViewModel;
    String currentUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        currentUid = getIntent().getStringExtra("uid");
        if (currentUid == null) {
            finish();
        }
        ePassword = findViewById(R.id.epassword);
        econfirmPassword = findViewById(R.id.econfirmpassword);
        buttonConfirm = findViewById(R.id.btnxacnhan);
        buttonCancel = findViewById(R.id.btncancel);
        progressBar = findViewById(R.id.progressbar);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            buttonConfirm.setEnabled(!isLoading? true : false);
        });
        authViewModel.getUpdateSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Utils.noti(ChangePass.this, "Đổi mật khẩu thành công");
                finish();
            }
            else {
                Utils.noti(ChangePass.this, "Đổi mật khẩu thất bại");
                finish();
            }});
        authViewModel.getUpdateMessage().observe(this, message -> {
            if (message != null) {
                Utils.noti(ChangePass.this, message);
            }
        });
        buttonConfirm.setOnClickListener(v -> {
            if (ePassword.getText().toString().isEmpty()) {
                ePassword.setError("Vui lòng nhập mật khẩu mới");
                return;
            }
            if (ePassword.getText().toString().length() < 6) {
                ePassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            if (!ePassword.getText().toString().equals(econfirmPassword.getText().toString())) {
                econfirmPassword.setError("Mật khẩu không khớp");
                return;
            }
           authViewModel.updatePass(currentUid, ePassword.getText().toString());
        });
        buttonCancel.setOnClickListener(v -> {
            finish();
        });

    }
}