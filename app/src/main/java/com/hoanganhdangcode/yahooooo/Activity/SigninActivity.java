package com.hoanganhdangcode.yahooooo.Activity;

import static java.lang.System.exit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hoanganhdangcode.yahooooo.Model.UserLogin;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

public class SigninActivity extends AppCompatActivity {
    EditText ephone, epassword;
    String phone, password;
    Button btnsignin, btnsignup,btnforgotpassword;
    ImageButton show1;
    int showed1=0;
    ImageView icon;
    int trytime=3+1;




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
        btnsignin.setOnClickListener(v -> {
            getdata();
            if (checkdata()){
                String hp = UtilsCrypto.md5(phone);
                UtilsDB.read("userlogin",hp, UserLogin.class, new UtilsDB.ReadCallback<UserLogin>() {
                    @Override
                    public void onSuccess(UserLogin result) {

                       if (!result.getHashpass().equals(UtilsCrypto.md5(password+result.getSalt()))) {
                            Utils.noti(SigninActivity.this, "Lỗi: mật khẩu không đúng, còn trytime lần thử");
                            if (trytime--<=0){
                                Utils.noti(SigninActivity.this, "Quá nhiều lần thử, vui lòng thử lại sau");
                                exit(0);
                            }


                        } else {
                           Utils.savepref(SigninActivity.this,"logined", "uid",result.getUid());
                           Utils.noti(SigninActivity.this, "Thành công");
                            Intent i1 = new Intent(SigninActivity.this, HomeActivity.class);
                           startActivity(i1);
                            finish();
                        }

                    }
                    @Override
                    public void onNotFound(){
                        Utils.noti(SigninActivity.this, "Lỗi: tài khoản không tồn tại");
                        if (trytime--<=0){
                            Utils.noti(SigninActivity.this, "Quá nhiều lần thử, vui lòng thử lại sau");
                            exit(0);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Utils.noti(SigninActivity.this, "Lỗi: "+e.getMessage());
                        Log.e("SigninActivity", "Cho nay:");
                    }
                });



            }


        });

    }

    public void initview(){
        icon = findViewById(R.id.icon);

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
        if (!regexphone(phone)) {
            ephone.setError("Số điện thoại không hợp lệ");
            kt = false;
        }
    return kt;

    }

    public boolean regexphone(String s) {
        String regex = "^(03|05|07|08|09)[0-9]{8}$";
        return s.matches(regex);}


}





