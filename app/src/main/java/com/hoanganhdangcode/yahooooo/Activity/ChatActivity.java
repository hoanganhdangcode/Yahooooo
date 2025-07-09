package com.hoanganhdangcode.yahooooo.Activity;

import static com.hoanganhdangcode.yahooooo.Util.AppMng.accesstoken;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.id;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.serverip;
import static com.hoanganhdangcode.yahooooo.Util.AppMng.tokensaphethan;
import static com.hoanganhdangcode.yahooooo.Util.HttpUtils.getJsonWithToken;
import static com.hoanganhdangcode.yahooooo.Util.HttpUtils.postJsonWithToken;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Adapter.MessageAdapter;
import com.hoanganhdangcode.yahooooo.Model.UserMessage;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.AppMng;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.MessageViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton btnSend, btngalery,btnback;
    private MessageAdapter adapter;
    private MessageViewModel messageViewModel;
    private String chatId, currentUid;
    private CircleImageView chatImage;
    private TextView chatName, userStatus, emptychat;
    private ActivityResultLauncher<Intent> mediaPickerLauncher;
    boolean isOnline = false; // Biến để theo dõi trạng thái online

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatid");
        currentUid = AppMng.id+""  ;

        initView();
        setupViewModel();
        setupMediaPicker();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
        recyclerView = findViewById(R.id.rvchat);
        editText = findViewById(R.id.emessage);
        btnSend = findViewById(R.id.send);
        chatImage = findViewById(R.id.chatimage);
        chatName = findViewById(R.id.chatname);
        userStatus = findViewById(R.id.userstatus);
        btngalery = findViewById(R.id.library);
        emptychat = findViewById(R.id.emptychat);
        btnback = findViewById(R.id.back);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        String uids[]  = chatId.split("_");
        if (uids.length == 2) {
            if (uids[0].equals(currentUid)) {
                checktoken(uids[1]);
            } else {
                checktoken(uids[0]);
            }
        } else {
            userStatus.setVisibility(View.GONE);
        }

    }

    private void setupViewModel() {
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.initialize(chatId, currentUid, this);
        messageViewModel.getdatachat(currentUid, chatId);

        messageViewModel.getNamechat().observe(this, chatName::setText);
        messageViewModel.getAvatarchat().observe(this, avatar -> Glide.with(this).load(avatar).placeholder(R.drawable.avatardefault).into(chatImage));

        messageViewModel.getMessages().observe(this, messages -> {
            if (adapter == null) {
                adapter = new MessageAdapter(this, currentUid,
                        (HashMap<String, String>) messageViewModel.getNameMap(),
                        (HashMap<String, String>) messageViewModel.getAvatarMap());
                recyclerView.setAdapter(adapter);
            }
            emptychat.setVisibility(messages.size() == 0 ? View.VISIBLE : View.GONE);
            adapter.setMessages(messages);
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        btnSend.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                UserMessage message = new UserMessage(chatId, Utils.genuuid(), currentUid, text, 1, 1, Utils.gettime(), null, 0, null);
                messageViewModel.sendMessage(message);
                editText.setText("");
            } else {
                Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            }

        });
        btnback.setOnClickListener(v -> {
            finish();
        });

        btngalery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            mediaPickerLauncher.launch(intent);
        });
    }
    public void checktoken(String uid){
        if(AppMng.tokenhethan()){
            AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {
                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                    checkonline(accessToken,uid);
                }


                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                }
            });
        }
        else{
            checkonline(accesstoken,uid);
            if (tokensaphethan()){AppMng.refreshtokendata(new AppMng.RefreshCallback() {
                @Override
                public void onRefreshSuccess(long id, String accessToken, String refreshToken) {

                    Log.d("MenuFragment", "Access token refreshed successfully: " + accessToken);
                }

                @Override
                public void onRefreshFailure() {
                    Log.d("MenuFragment", "Access token refresh failed");
                }

            });}
        }

    }
    private void checkonline (String actk,String uid){
        getJsonWithToken("http://" + serverip + ":8080/getonline?userid="+uid+"", actk, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GETONLINE", "Lỗi kết nối: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    // xử lý JSON ở đây
                    try {
                        JSONObject obj = new JSONObject(res);
                        boolean online = obj.getBoolean("online");
                        if (online) {
                            isOnline = true;
                            userStatus.setText("Online");
                        } else {
                            isOnline = false;
                            userStatus.setText("Ofline");
                        }


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
                        List<Uri> uriList = new ArrayList<>();

                        if (result.getData().getClipData() != null) {
                            for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                                uriList.add(result.getData().getClipData().getItemAt(i).getUri());
                            }
                        } else if (result.getData().getData() != null) {
                            uriList.add(result.getData().getData());
                        }

                        for (Uri uri : uriList) {
                            int type = getMimeType(uri);
                            messageViewModel.sendMessage(new UserMessage(chatId,Utils.genuuid(),currentUid,"",type,0,Utils.gettime(),null,0,uri.toString()));
                        }
                    }
                });
    }



    private int getMimeType(Uri uri) {
        String mime = getContentResolver().getType(uri);
        if (mime == null) return 1;
        if (mime.startsWith("image/")) return 2;
        if (mime.startsWith("video/")) return 3;
        return 1;
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageViewModel.clear();
        Log.d("ChatActivity", "onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageViewModel.resume();
        Log.d("ChatActivity", "onResume called");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        messageViewModel.resume();
        Log.d("ChatActivity", "onRestart called");
    }
}
