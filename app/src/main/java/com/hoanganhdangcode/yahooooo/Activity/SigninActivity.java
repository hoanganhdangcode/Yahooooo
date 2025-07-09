package com.hoanganhdangcode.yahooooo.Activity;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.id;
import static java.lang.System.exit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hbb20.CountryCodePicker;
import com.hoanganhdangcode.yahooooo.Model.UserLogin;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.LocaleHelper;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.AuthViewModel;

public class SigninActivity extends AppCompatActivity {
    EditText ephone, epassword;
    String phone, password;
    Button btnsignin, btnsignup,btnforgotpassword;
    ImageButton show1,nightmode;
    int showed1=0;
    ImageView icon;
    int trytime=3+1;
    private AuthViewModel authViewModel;
    ProgressBar progressBar;
    Boolean isnightmode;
    CountryCodePicker ccp;
    String language;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initview();


    }

    public void initview(){
      settingnightmode();


        icon = findViewById(R.id.icon);
        progressBar = findViewById(R.id.progressbar);
        btnsignin = findViewById(R.id.btnsignin);
        btnsignup = findViewById(R.id.btnsignup);
        btnforgotpassword = findViewById(R.id.btnfogotpass);
        ephone = findViewById(R.id.ephone);
        epassword = findViewById(R.id.epassword);
        show1 = findViewById(R.id.show1);
        show1.setOnClickListener(v->{
            if (showed1++%2==0){
                Utils.showpass(epassword);
            }
            else
            {
                Utils.hiddenpass(epassword);
            }
        });
        btnsignup.setOnClickListener(v->{
            Intent i2 = new Intent(SigninActivity.this,SignupActivity.class);
            startActivity(i2);
        });
        btnforgotpassword.setOnClickListener(v->{
            Intent i3 = new Intent(SigninActivity.this,FogotPassActivity.class);
            startActivity(i3);
        });

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getLoginMessage().observe(this, msg ->
                Utils.noti(SigninActivity.this,msg));
        authViewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                Intent i= new Intent(SigninActivity.this,HomeActivity.class);
                SharedPreferences preferences = getSharedPreferences("logined", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("id", id);
                editor.putString("uid",String.valueOf(id));
                editor.putBoolean("logined", true);
                editor.putString("accesstoken", AppMng.accesstoken);
                editor.putString("refreshtoken", AppMng.refreshtoken);
                editor.apply();
                startActivity(i);
                finish();
            }
        });
        authViewModel.getIsLoading().observe(this, isLoading -> {
            btnsignin.setText(isLoading?"":"Đăng nhập");
            btnsignin.setEnabled(isLoading?false:true);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        btnsignin.setOnClickListener(v -> {
            getdata();
            if (checkdata()){
                authViewModel.login(phone, password);
            }
        });
    }
    public void getdata() {
        phone=ephone.getText().toString();
        password=epassword.getText().toString();
    }
    public boolean checkdata() {
        boolean kt = true;
        if (phone.equals("")) {
            ephone.setError("Vui lòng nhập số điện thoại");
            kt = false;
        }
        if (password.equals("")) {
            epassword.setError("Vui lòng nhập mật khẩu");
            kt = false;
        }
        if (!Utils.regexphone(phone)) {
            ephone.setError("Số điện thoại không hợp lệ");
            kt = false;
        }
    return kt;
    }
    public void settingnightmode(){
        nightmode = findViewById(R.id.nightmode);
        SharedPreferences sharedPreferences = getSharedPreferences("nightmode", MODE_PRIVATE);
        isnightmode = sharedPreferences.getBoolean("nightmode", false);
        if (isnightmode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightmode.setImageResource(R.drawable.sunset);
        } else{                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightmode.setImageResource(R.drawable.night);
        }

        nightmode.setOnClickListener(v->{
            isnightmode = !isnightmode;
            SharedPreferences preferences = getSharedPreferences("nightmode", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("nightmode", isnightmode);
            editor.apply();
            if (isnightmode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nightmode.setImageResource(R.drawable.sunset);
            } else{ AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                nightmode.setImageResource(R.drawable.night);
            }
        });
    }




}





