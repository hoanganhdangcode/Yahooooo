package com.hoanganhdangcode.yahooooo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.hoanganhdangcode.yahooooo.Activity.ProfileActivity;
import com.hoanganhdangcode.yahooooo.Adapter.FriendAdapter;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Repository.ChatRepository;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.FriendViewModel;

import java.util.ArrayList;

public class FriendFragment extends Fragment {
    private String thisuid;
    private RadioGroup radioGroup;
    private RecyclerView rvfriend;
    private ProgressBar progressBar;
    private FriendAdapter adapter;
    private FriendViewModel friendViewModel;
    private ChatRepository chatRepository;
    private SearchView searchView;

    TextView tvempty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        thisuid = new Utils().getpref(getContext(), "logined", "uid");
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        friendViewModel.setCurrentUid(thisuid);

        radioGroup = view.findViewById(R.id.phanloai);
        rvfriend = view.findViewById(R.id.friendlist);
        progressBar = view.findViewById(R.id.progressbar);
        tvempty = view.findViewById(R.id.danhsachtrong);
        searchView = view.findViewById(R.id.searchbut);


        adapter = new FriendAdapter(getContext(), new ArrayList<>(), new FriendAdapter.OnFriendActionListener() {
            @Override
            public void onAdd(String uid, int status) {
                int st1 = -1, st2 = -1;
                if (status < 0) { st1 = 3; st2 = 4; }
                else if (status == 3 || status == 5) { st1 = -1; st2 = -1; }
                else if (status == 4) { st1 = 5; st2 = 5; }
                adapter.showLoadingFor(uid);
                friendViewModel.changeFriendStatus(thisuid, uid, st1, st2);
            }

            @Override
            public void onCancel(String uid) {
                adapter.showLoadingFor(uid);
                friendViewModel.changeFriendStatus(thisuid, uid, -1, -1);
            }

            @Override
            public void onChat(String uid, int status) {
                try {
                    if (chatRepository == null) chatRepository = new ChatRepository();
                    if (thisuid == null) thisuid = Utils.getpref(getContext(), "logined", "uid");

                    chatRepository.createPrivateChatIfNotExists(thisuid, uid, gettype(status), new ChatRepository.OnChatCreated() {
                        @Override
                        public void onSuccess(String chatid) {
                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            intent.putExtra("chatid", chatid);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("FriendFragment", "Create chat failed: " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.e("FriendFragment", "onChat Exception", e);
                }
            }


            @Override
            public void onViewProfile(String uid) {
                startActivity(new Intent(getContext(), ProfileActivity.class).putExtra("uid", uid));
            }

            @Override
            public void onBlock(String uid, int status) {
                adapter.showLoadingFor(uid);
                if (status == 2) friendViewModel.changeFriendStatus(thisuid, uid, -1, -1);
                else friendViewModel.changeFriendStatus(thisuid, uid, 2, 1);
            }
        });

        rvfriend.setLayoutManager(new LinearLayoutManager(getContext()));
        rvfriend.setAdapter(adapter);

        friendViewModel.getFriendListLiveData().observe(getViewLifecycleOwner(), list -> {
            adapter.setFriendList(list);
            if (list.size()==0){tvempty.setVisibility(View.VISIBLE);}else{tvempty.setVisibility(View.GONE);}
        });

        friendViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        friendViewModel.getIsActionLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (!isLoading) adapter.showLoadingFor(null);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                friendViewModel.setKeyword(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendViewModel.setKeyword(newText);
                return true;
            }
        });
        if (radioGroup.getCheckedRadioButtonId()==R.id.tatca)friendViewModel.setFilterType("ALL");
        else if (radioGroup.getCheckedRadioButtonId() == R.id.banbe) friendViewModel.setFilterType("FRIENDS");
        else if (radioGroup.getCheckedRadioButtonId() == R.id.loimoi) friendViewModel.setFilterType("INVITES");
        else if (radioGroup.getCheckedRadioButtonId() == R.id.chan) friendViewModel.setFilterType("BLOCKED");
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tatca) friendViewModel.setFilterType("ALL");
            else if (checkedId == R.id.banbe) friendViewModel.setFilterType("FRIENDS");
            else if (checkedId == R.id.loimoi) friendViewModel.setFilterType("INVITES");
            else if (checkedId == R.id.chan) friendViewModel.setFilterType("BLOCKED");
        });
    }
    private int gettype(int i) {
        if (i == 1 || i == 2) return -1;
        if (i == 5) return 1;
        return 0;
    }
}