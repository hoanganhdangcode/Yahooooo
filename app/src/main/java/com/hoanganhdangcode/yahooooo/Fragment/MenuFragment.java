package com.hoanganhdangcode.yahooooo.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.accesstoken;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.id;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.serverip;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.tokensaphethan;
import static com.hoanganhdangcode.yahooooo.Util.HttpUtils.postJsonWithToken;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hoanganhdangcode.yahooooo.Activity.CheckPass;
import com.hoanganhdangcode.yahooooo.Activity.FogotPassActivity;
import com.hoanganhdangcode.yahooooo.Activity.OtpSubmitActivity;
import com.hoanganhdangcode.yahooooo.Activity.ProfileActivity;
import com.hoanganhdangcode.yahooooo.Activity.SplashActivity;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Repository.CloudinaryUploader;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.HttpUtils;
import com.hoanganhdangcode.yahooooo.Util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MenuFragment extends Fragment {
    Button btnsignout,btnchangepassword,btnwall;
    Switch nightmode;
    String currentuid;
    TextView tname,tdes;
    ImageView tbackground;
    CircleImageView tavatar;
    String ename,edes,eavatar,ebackground="Loading...";
    Button btneditavatar,btneditbackground,btneditname,btneditdes;
    public static int currentAction = 1; // 1: đổi avatar, 2: đổi background, 3: đổi tên, 4: đổi mô tả
    private ActivityResultLauncher<Intent> mediaPickerLauncher;


//    WallViewModel wallViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        currentuid = Utils.getpref(getContext(),"logined","uid");
        if (currentuid == null){
            Intent intent = new Intent(getContext(), SplashActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        initview(view);
        checktoken();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            checktoken(); // Gọi hàm tải dữ liệu
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false); //
            }, 2000); // giả sử delay 2s
        });
        btneditavatar = view.findViewById(R.id.btneditavatar);
        btneditavatar.setOnClickListener(v->{

             currentAction = 1; // ví dụ 1 là đổi avatar
            pickImage(1);
        });
        btneditbackground = view.findViewById(R.id.btneditbackground);
        btneditbackground.setOnClickListener(v->{
            currentAction = 2; // ví dụ 2 là đổi background
            pickImage(2);
        });
        btneditname = view.findViewById(R.id.btneditname);
        btneditname.setOnClickListener(v->{
            currentAction = 3; // ví dụ 3 là đổi tên
            opendialog(3);

        });
        btneditdes = view.findViewById(R.id.btneditdes);
        btneditdes.setOnClickListener(v->{
            currentAction = 4; // ví dụ 4 là đổi mô tả
            opendialog(4);

        });
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

    private void pickImage(int i) {
        currentAction = i;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        mediaPickerLauncher.launch(intent);
    }
    private void opendialog(int i) {
        String title = "";
       if(i ==3){
           currentAction = 3; // Đổi tên
              title = "Đổi tên";
       }
       else{
           currentAction = 4; // Đổi mô tả
           title = "Đổi mô tả";
       }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_text, null);
        bottomSheetDialog.setContentView(dialogView);
        TextView titleTextView = dialogView.findViewById(R.id.title);
        titleTextView.setText(title);
        TextView inputTextView = dialogView.findViewById(R.id.editText);
        Button saveButton = dialogView.findViewById(R.id.submitButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        saveButton.setOnClickListener(v -> {
            String value = inputTextView.getText().toString().trim();
            if (value.isEmpty()) {
                Utils.noti(getContext(), "Vui lòng nhập " );
            } else {
                updateme(i, value);
                bottomSheetDialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void updateme(int i,String value) {
        if(AppMng.tokenhethan()){
            AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Utils.savepref(getContext(),"logined","accesstoken", accessToken);
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                    sendrequest(i, accessToken,value);
                }


                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                    Utils.savepref(getContext(),"logined","uid", null);
                    Utils.xoapref(getContext(),"logined");
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    Utils.noti(getContext(), "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
        else{
            sendrequest(i,accesstoken,value);

            if (tokensaphethan()){AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Utils.savepref(getContext(),"logined","accesstoken", accessToken);
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                }

                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                    Utils.savepref(getContext(),"logined","uid", null);
                    Utils.xoapref(getContext(),"logined");
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    Utils.noti(getContext(), "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                    startActivity(intent);
                    getActivity().finish();
                }

            });}
        }
    }
    private void sendrequest(int i, String actk, String value) {
        String json = "";
        if(i==3){ json = "{\"name\":\"" + value + "\"}";
        }
        else if(i==2){ json = "{\"background\":\"" + value + "\"}";
        }
        else if(i==1){ json = "{\"avatar\":\"" + value + "\"}";
        }
        else if(i==4){ json = "{\"description\":\"" + value + "\"}";
        }
        postJsonWithToken("http://" + serverip + ":8080/user/updateme", json, actk, new Callback() {
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

                        getActivity().runOnUiThread(() -> {
                            tname.setText(ename);
                            tdes.setText(edes);
                            Glide.with(getContext()).load(eavatar).placeholder(R.drawable.avatardefault).into(tavatar);
                            Glide.with(getContext()).load(ebackground).placeholder(R.drawable.backgroundefault).into(tbackground);
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
    private void setupMediaPicker() {
        mediaPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            switch (currentAction) {
                                case 1:
                                    upcloudinary(1, imageUri);
                                    break;
                                case 2:
                                   upcloudinary(2, imageUri);
                                    break;
                            }
                        }
                    }
                }
        );
    }

    private void upcloudinary(int i, Uri imageUri) {
      new Thread(() -> {
            CloudinaryUploader.uploadImage(getContext(), imageUri, new CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String url) {
                    Log.d("MenuFragment", "Upload thành công: " + url);
                    updateme(i, url);
                }

                @Override
                public void onFailure(String error) {
                    Log.e("MenuFragment", "Upload thất bại: " + error);
                    getActivity().runOnUiThread(() -> {
                        Utils.noti(getContext(), "Upload thất bại: " + error);
                    });
                }
            });
        }).start();
    }

    public void initview(View view){
         tname = view.findViewById(R.id.username);
        tdes= view.findViewById(R.id.description);
         tavatar = view.findViewById(R.id.avatar);
         tbackground= view.findViewById(R.id.background);
         setupMediaPicker();
        btnwall = view.findViewById(R.id.btnwall);
        btnwall.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("uid", currentuid);
            startActivity(intent);
        });

        btnchangepassword = view.findViewById(R.id.btnchangepass);
        btnchangepassword.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), CheckPass.class);
            Log.d("btnchangepassword", btnchangepassword.toString());
            intent.putExtra("work", "change");
            startActivity(intent);
        });
        btnsignout = view.findViewById(R.id.btnsignout);
        btnsignout.setOnClickListener(v->{
            Utils.savepref(getContext(),"logined","uid", null);
            Utils.xoapref(getContext(),"logined");
            Intent intent = new Intent(getContext(), SplashActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("nightmode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        nightmode = view.findViewById(R.id.nighttheme);
        nightmode.setChecked(sharedPreferences.getBoolean("nightmode", false));
        nightmode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("nightmode", true);
                editor.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                editor.putBoolean("nightmode", false);
                editor.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
    public void checktoken(){
        if(AppMng.tokenhethan()){
            AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Utils.savepref(getContext(),"logined","accesstoken", accessToken);
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                    getdata(accessToken);
                }


                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                    Utils.savepref(getContext(),"logined","uid", null);
                    Utils.xoapref(getContext(),"logined");
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    Utils.noti(getContext(), "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
        else{
            getdata(Utils.getpref(getContext(),"logined","accesstoken"));

            if (tokensaphethan()){AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Utils.savepref(getContext(),"logined","accesstoken", accessToken);
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                }

                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                    Utils.savepref(getContext(),"logined","uid", null);
                    Utils.xoapref(getContext(),"logined");
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    Utils.noti(getContext(), "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
                    startActivity(intent);
                    getActivity().finish();
                }

            });}
        }

    }
    private void getdata(String actk) {
        String json = "{\"id\":\"" + id + "\"}";
        postJsonWithToken("http://" + serverip + ":8080/user/getme", json, actk, new Callback() {
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

                        getActivity().runOnUiThread(() -> {
                            tname.setText(ename);
                            tdes.setText(edes);
                            Glide.with(getContext()).load(eavatar).placeholder(R.drawable.avatardefault).into(tavatar);
                            Glide.with(getContext()).load(ebackground).placeholder(R.drawable.backgroundefault).into(tbackground);
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