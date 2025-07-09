package com.hoanganhdangcode.yahooooo.Activity;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.id;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.serverip;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.tokensaphethan;
import static com.hoanganhdangcode.yahooooo.Util.HttpUtils.postJsonWithToken;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.WallViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {
    String targetuid;
    String currentuid;
    TextView tname,tdes;
    ImageView tbackground;
    CircleImageView tavatar;
    String ename,edes,eavatar,ebackground="Loading...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(ProfileActivity.this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        targetuid = getIntent().getStringExtra("uid");
        currentuid = Utils.getpref(ProfileActivity.this,"logined","uid");
         tavatar = findViewById(R.id.avatar);
                tbackground = findViewById(R.id.background);
         tname = findViewById(R.id.username);
         tdes = findViewById(R.id.description);

        if (targetuid == null || targetuid.isEmpty()) {
            finish();
        }
        checktoken();
        tavatar.setOnClickListener(v -> {
            if (eavatar != null && !eavatar.isEmpty()) {
                openImg(eavatar);
            }
            else{
                openImg(Utils.urlavatardefault);
            }
        });
        tbackground.setOnClickListener(v -> {
            if (ebackground != null && !ebackground.isEmpty()) {
                openImg(ebackground);
            }
            else{
                openImg(Utils.urlbackgroundefault);
            }
        });


    }
    public void checktoken(){
        if(AppMng.tokenhethan()){
            AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Utils.savepref(ProfileActivity.this,"logined","accesstoken", accessToken);
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                    getdata(accessToken);
                }


                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                    Utils.xoapref(ProfileActivity.this,"logined");
                    Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                    Utils.noti(ProfileActivity.this, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                    startActivity(intent);
                    finish();
                }
            });
        }
        else{
            getdata(Utils.getpref(ProfileActivity.this,"logined","accesstoken"));

            if (tokensaphethan()){AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Utils.savepref(ProfileActivity.this,"logined","accesstoken", accessToken);
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                }

                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                    Utils.savepref(ProfileActivity.this,"logined","uid", null);
                    Utils.xoapref(ProfileActivity.this,"logined");
                    Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                    Utils.noti(ProfileActivity.this, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                    startActivity(intent);
                   finish();
                }

            });}
        }

    }
    private void getdata(String actk) {
        String json = "{\"idstr\":\"" + targetuid + "\"}";
        postJsonWithToken("http://" + serverip + ":8080/user/getuserprofile", json, actk, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("POST", "Lỗi kết nối: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    // xử lý JSON ở đây
                    try {
                        JSONObject obj = new JSONObject(res);
                        ename = obj.getString("name");
                        edes = obj.getString("description");
                        eavatar = obj.getString("avatar");
                        ebackground = obj.getString("background");

                        runOnUiThread(() -> {
                            tname.setText(ename);
                            tdes.setText(edes);
                            Glide.with(ProfileActivity.this).load(eavatar).placeholder(R.drawable.avatardefault).into(tavatar);
                            Glide.with(ProfileActivity.this).load(ebackground).placeholder(R.drawable.backgroundefault).into(tbackground);
                        });
                    } catch (JSONException e) {
                        Log.e("POST", "Lỗi phân tích JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("POST", "Lỗi server: " + response.code());
                }
            }
        });

    }

    private void openImg(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}