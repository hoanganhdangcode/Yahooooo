package com.hoanganhdangcode.yahooooo.Activity;

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
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.MessageViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton btnSend, btngalery;
    private MessageAdapter adapter;
    private MessageViewModel messageViewModel;
    private String chatId, currentUid;
    private CircleImageView chatImage;
    private TextView chatName, userStatus, emptychat;
    private ActivityResultLauncher<Intent> mediaPickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatid");
        currentUid = new Utils().getpref(this, "logined", "uid");

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setupViewModel() {
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.initialize(chatId, currentUid, this);
        messageViewModel.getdatachat(currentUid, chatId);

        messageViewModel.getNamechat().observe(this, chatName::setText);
        messageViewModel.getAvatarchat().observe(this, avatar -> Glide.with(this).load(avatar).into(chatImage));

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

        btngalery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            mediaPickerLauncher.launch(intent);
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
