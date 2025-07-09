package com.hoanganhdangcode.yahooooo.Activity;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.serverip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.HttpUtils;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
                    String json = "{\"phone\":\"" + phone + "\"}";

                    HttpUtils.postJson("http://" + serverip + ":8080/auth/sendotp", json, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("OTP", "Gửi thất bại: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String res = response.body().string();
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(res);
                                     AppMng.id = obj.getLong("id");
                                    runOnUiThread(() -> {
                                        Utils.noti(FogotPassActivity.this, "Đã gửi OTP đến số điện thoại " + phone);
                                        Intent intent = new Intent(FogotPassActivity.this, OtpSubmitActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });

                                } catch (JSONException e) {
                                    Log.e("OTP", "Lỗi phân tích JSON: " + e.getMessage());

                                }
                            } else {
                                Log.e("OTP", ": " + response.code());
                                    runOnUiThread( () -> {
                                        Utils.noti(FogotPassActivity.this, response.message() + trytime + " lần thử");
                                        if (--trytime < 0) {
                                            Utils.noti(FogotPassActivity.this, "Quá nhiều lần thử");
                                            System.exit(0);
                                            finish();
                                        }
                                    });

                        }
                    }});

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