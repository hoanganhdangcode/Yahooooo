package com.hoanganhdangcode.yahooooo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

public class FogotPassActivity extends AppCompatActivity {
    EditText ephone;
    String phone;
    Button btnfogotpass;
    int trytime = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fogot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initview();
    }
    public void  initview(){
        ephone = findViewById(R.id.ephone);
        btnfogotpass = findViewById(R.id.btnguiotp);

        btnfogotpass.setOnClickListener(v->{
            getdata();
                if (checkdata()){
                    UtilsDB.check("userlogin", phone, new UtilsDB.CheckCallback() {
                        @Override
                        public void onSuccess(String uid) {
                            if (uid != null){
                                Intent i = new Intent(FogotPassActivity.this, OtpSubmitActivity.class);
                                i.putExtra("phone",phone);
                                i.putExtra("uid",uid);
                                startActivity(i);
                                finish();

                            }
                            else{
                                Utils.noti(FogotPassActivity.this, "Số điện thoại không tồn tại, còn "+trytime+" lần thử");
                                if (--trytime<0){
                                    Utils.noti(FogotPassActivity.this, "Quá nhiều lần thử");
                                    System.exit(0);
                                    finish();

                                }

                            }

                        }

                        @Override
                        public void onFailure(Exception e) {
                            Utils.noti(FogotPassActivity.this, "Lỗi: "+e.getMessage());
                        }


                    });

            }
        });

    }
    public void getdata() {

        phone=ephone.getText().toString();

    }
    public boolean checkdata() {
        boolean kt = true;
        if (phone.equals("")) {
            ephone.setError("Vui lòng nhập số điện thoại");
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