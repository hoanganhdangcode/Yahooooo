package com.hoanganhdangcode.yahooooo.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.bumptech.glide.util.Util;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hoanganhdangcode.yahooooo.Activity.ChatActivity;
import com.hoanganhdangcode.yahooooo.Model.UserData;
import com.hoanganhdangcode.yahooooo.Model.UserFriend;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

import java.util.ArrayList;
import java.util.List;



public class FriendFragment extends Fragment {

    String thisuid;
    String search;
    EditText esearch;
    ImageButton bsearch;
    RadioGroup radioGroup;
    List<UserFriend> tatcalist, banbelist, loimoilist, chanlist;
    List<UserData> tatcadata, banbedata, loimoidata, chandata,listhienthi;
    UserFriend searchfriend;
    String timkiem;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_friend, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load Child Fragment vào Parent Fragment
        tatcalist=new ArrayList<>();
        banbelist=new ArrayList<>();
        loimoilist=new ArrayList<>();
        chanlist=new ArrayList<>();
        tatcadata=new ArrayList<>();
        banbedata=new ArrayList<>();
        loimoidata=new ArrayList<>();
        chandata=new ArrayList<>();
        listhienthi=new ArrayList<>();
        initview(view);
        getData();




    }

    private void initview(View view) {
        thisuid = Utils.getpref(getContext(), "logined", "uid");
        esearch=view.findViewById(R.id.esearch);
        radioGroup = view.findViewById(R.id.phanloai);
        bsearch = view.findViewById(R.id.searchicon);
        esearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0) {

                    if (Utils.regexphone(s.toString())){
                        String hashphone = UtilsCrypto.md5(s.toString());
                        UtilsDB.getuid(hashphone, new UtilsDB.GetuidCallback() {
                            @Override
                            public void onExist(String uid) {
                                if (uid!=null){
                                    timkiem=uid;
                                    Utils.noti(getActivity(), "Tìm kiếm: "+timkiem);
                                }
                            }
                                @Override
                               public void onNotExist()  {
                                    Utils.noti(getActivity(), "Không tìm thấy");
                                }


                            @Override
                            public void onFailure(Exception e) {
                                Utils.noti(getActivity(), "Lỗi kết nối");

                            }
                        });
                        bsearch.setOnClickListener(v->{
                        });

                    }
                }
            }
        });

    }
    public void getData() {
        db.collection("userfriend")
                .document(thisuid)
                .collection(thisuid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot){
                            tatcalist.add( document.toObject(UserFriend.class));
                        }

                    }
                    else {
                        Utils.noti(getActivity(), "Lỗi kết nối");
                    }
                });






    }



}