package com.hoanganhdangcode.yahooooo.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hoanganhdangcode.yahooooo.Adapter.HomeFragmentAdapter;
import com.hoanganhdangcode.yahooooo.Fragment.ChatFragment;
import com.hoanganhdangcode.yahooooo.Fragment.FeedFragment;
import com.hoanganhdangcode.yahooooo.Fragment.FriendFragment;
import com.hoanganhdangcode.yahooooo.Fragment.MenuFragment;
import com.hoanganhdangcode.yahooooo.Fragment.NotificationFragment;
import com.hoanganhdangcode.yahooooo.Model.NotificationToken;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

public class HomeActivity extends AppCompatActivity {
     BottomNavigationView bottomNavigationView;
     ViewPager2 viewPager2;
     private String currentuid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (!Utils.tontaipref(this, "logined")){
            finish();
        }
        else{
            currentuid = Utils.getpref(this, "logined", "uid");
            Log.d("UID", "Current UID: " + currentuid);
        }
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
//            else if (item.getItemId()==R.id.nav_feed){
//                viewPager2.setCurrentItem(2);
//            }
//            else if (item.getItemId()==R.id.nav_notification){
//                viewPager2.setCurrentItem(3);}
            //nav menu=======================
//                <item
//            android:id="@+id/nav_feed"
//            android:icon="@drawable/ic_nav_feed"
//            app:itemBackground="@android:color/transparent"
//            android:title="@string/nav_3" />
//
//    <item
//            android:id="@+id/nav_notification"
//            android:icon="@drawable/ic_notification"
//            app:itemBackground="@android:color/transparent"
//
//            android:title="@string/nav_4"
//                    />
            //-
            else if (item.getItemId()==R.id.nav_menu){
                viewPager2.setCurrentItem(2);
            }
            return true;

        });
        registerTokenNoti();

}

    private void registerTokenNoti() {


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d("FCM_TOKEN", token);
                        if (Utils.tontaipref(this, "token"+ currentuid)) {
                            Log.d("FCM_TOKEN", "Token already exists");
                        }
                        else {
                            String deviceId = Utils.getdeviceid(this);
                            UtilsDB.create("token", deviceId, new NotificationToken(deviceId, currentuid, token),
                                    new UtilsDB.CreateCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("FCM Token", "Token saved successfully");
                                           Utils.savepref(HomeActivity.this, "token"+ currentuid, "token", token);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("FCM Token", "Failed to save token", e);
                                            Snackbar.make(findViewById(android.R.id.content),
                                                    "Lỗi tạm thời không hiển thị thông báo",
                                                    Snackbar.LENGTH_LONG).show();

                                        }
                                    }
                            );

                        }
                        // TODO: Lưu token hoặc gửi lên server

                    } else {
                        Log.e("FCM Token", "Failed to get token", task.getException());
                        Snackbar.make(findViewById(android.R.id.content),
                                "Failed to get FCM token",
                                Snackbar.LENGTH_LONG).show();
                    }
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



