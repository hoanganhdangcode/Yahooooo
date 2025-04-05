package com.hoanganhdangcode.yahooooo.Fragment;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hoanganhdangcode.yahooooo.Model.UserChat;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChatFragment extends Fragment {
    ImageButton menu, notification, friendadd, search;
    EditText esearch;
    TextView notificationcount, friendcount;
    RecyclerView chatview;
    List<UserChat> listchat ;
    ConstraintLayout notificationview, friendview;
    int notishow=0, friendaddshow=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load Child Fragment vào Parent Fragment

   initview(view);

    }



    public void initview(View view){




    }
    public void capnhatlist(String s){
        if (s.equals("")){

        }
        else{

        }
    }
}