package com.hoanganhdangcode.yahooooo.Fragment;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.Activity.CheckPass;
import com.hoanganhdangcode.yahooooo.Activity.ProfileActivity;
import com.hoanganhdangcode.yahooooo.Activity.SplashActivity;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.AuthViewModel;
import com.hoanganhdangcode.yahooooo.ViewModel.WallViewModel;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MenuFragment extends Fragment {
    Button btnsignout,btnchangepassword,btnwall;
    Switch nightmode;
    String currentuid;

    WallViewModel wallViewModel;
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
    }
    public void initview(View view){
        TextView tname = view.findViewById(R.id.username),tdes= view.findViewById(R.id.description);
        ImageView tavatar = view.findViewById(R.id.avatar), tbackground= view.findViewById(R.id.background);
        wallViewModel = new ViewModelProvider(this).get(WallViewModel.class);
        wallViewModel.setTargetuid(currentuid);
        wallViewModel.loadData(currentuid,currentuid);
        wallViewModel.getName().observe(getViewLifecycleOwner(), name -> {
          tname.setText(name);
        });
        wallViewModel.getDes().observe(getViewLifecycleOwner(), des -> {
            tdes.setText(des);
        });
        wallViewModel.getAvatar().observe(getViewLifecycleOwner(), avatar -> {
                Glide.with(this).load(avatar).into(tavatar);

        });
        wallViewModel.getBackground().observe(getViewLifecycleOwner(), background -> {
                Glide.with(this).load(background).into(tbackground);
        });
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

}