package com.hoanganhdangcode.yahooooo.Fragment;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.hoanganhdangcode.yahooooo.Activity.SplashActivity;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MenuFragment extends Fragment {
    Button btnsignout;
    Switch nightmode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        initview(view);

    }



    public void initview(View view){
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