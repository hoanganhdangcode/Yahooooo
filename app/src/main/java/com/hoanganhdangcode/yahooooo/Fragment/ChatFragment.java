package com.hoanganhdangcode.yahooooo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hoanganhdangcode.yahooooo.Activity.ChatActivity;
import com.hoanganhdangcode.yahooooo.Adapter.ChatAdapter;
import com.hoanganhdangcode.yahooooo.Model.ChatDisplay;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.ChatViewModel;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;

    private EditText esearch;
    private ImageButton searchIcon;
    private RadioGroup radioGroup;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private String thisUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setupViewModel();
        setupEvents();
    }

    private void initView(View view) {

        esearch = view.findViewById(R.id.esearch);
        searchIcon = view.findViewById(R.id.searchicon);
        radioGroup = view.findViewById(R.id.phanloai);
        recyclerView = view.findViewById(R.id.rvchat);
        progressBar = view.findViewById(R.id.progressbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<>(), chatId -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatid", chatId);
            startActivity(intent);
        });
        recyclerView.setAdapter(chatAdapter);
    }

    private void setupViewModel() {
        thisUid =  Utils.getpref(getContext(), "logined", "uid");
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.setCurrentUid(thisUid);

        chatViewModel.getChatDisplayLiveData().observe(getViewLifecycleOwner(), chatList -> {
            chatAdapter.setChatList(chatList);
        });
        chatViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE:View.GONE);
        });
    }

    private void setupEvents() {
        chatViewModel.setFilter("ALL");
        // Search
        searchIcon.setOnClickListener(v -> {
            String keyword = esearch.getText().toString().trim();
            chatViewModel.setSearchKeyword(keyword);
        });

        esearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                chatViewModel.setSearchKeyword(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter                chatViewModel.setFilter("ALL");
        chatViewModel.setFilter("FRIEND");

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tatca) {
                chatViewModel.setFilter("ALL");
            } else if (checkedId == R.id.banbe) {
                chatViewModel.setFilter("FRIEND");
            } else if (checkedId == R.id.nhom) {
                chatViewModel.setFilter("GROUP");
            } else {
                chatViewModel.setFilter("WAITING");
            }
        });

    }
}
