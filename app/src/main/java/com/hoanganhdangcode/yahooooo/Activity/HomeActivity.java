package com.hoanganhdangcode.yahooooo.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.hoanganhdangcode.yahooooo.Adapter.HomeFragmentAdapter;
import com.hoanganhdangcode.yahooooo.Fragment.ChatFragment;
import com.hoanganhdangcode.yahooooo.Fragment.FeedFragment;
import com.hoanganhdangcode.yahooooo.Fragment.FriendFragment;
import com.hoanganhdangcode.yahooooo.Fragment.MenuFragment;
import com.hoanganhdangcode.yahooooo.Fragment.NotificationFragment;
import com.hoanganhdangcode.yahooooo.R;
public class HomeActivity extends AppCompatActivity {
     BottomNavigationView bottomNavigationView;
     ViewPager2 viewPager2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.viewpager2);
        setupviewPager();
        viewPager2.setCurrentItem(2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId()==R.id.nav_chat){
                viewPager2.setCurrentItem(0);
            }else if (item.getItemId()==R.id.nav_friend){
                viewPager2.setCurrentItem(1);
            }
            else if (item.getItemId()==R.id.nav_feed){
                viewPager2.setCurrentItem(2);
            }
            else if (item.getItemId()==R.id.nav_notification){
                viewPager2.setCurrentItem(3);}
            else if (item.getItemId()==R.id.nav_menu){
                viewPager2.setCurrentItem(4);
            }
            return true;

        });


}

public void setupviewPager(){
        HomeFragmentAdapter homeFragmentAdapter = new HomeFragmentAdapter(this);
 viewPager2.setAdapter(homeFragmentAdapter);
    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
        }



});}
}



