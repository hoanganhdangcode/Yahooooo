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
import android.widget.SearchView;
import android.widget.TextView;

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
    private SearchView searchView;
    private RadioGroup radioGroup;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvempty;

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

        radioGroup = view.findViewById(R.id.phanloai);
        recyclerView = view.findViewById(R.id.rvchat);
        progressBar = view.findViewById(R.id.progressbar);
        searchView = view.findViewById(R.id.searchbut);
        tvempty = view.findViewById(R.id.danhsachtrong);


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
            chatAdapter.notifyDataSetChanged();
            tvempty.setVisibility(chatList.isEmpty() ? View.VISIBLE : View.GONE);
        });
        chatViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE:View.GONE);
        });
    }

    private void setupEvents() {
        chatViewModel.setFilter("ALL");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                chatViewModel.setSearchKeyword(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatViewModel.setSearchKeyword(newText);
                return true;
            }
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
