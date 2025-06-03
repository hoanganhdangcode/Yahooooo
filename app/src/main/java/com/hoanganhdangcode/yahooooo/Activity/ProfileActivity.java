package com.hoanganhdangcode.yahooooo.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.WallViewModel;

public class ProfileActivity extends AppCompatActivity {
    String targetuid;
    String currentuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        targetuid = getIntent().getStringExtra("uid");
        currentuid = Utils.getpref(this,"logined","uid");
        ImageView tavatar = findViewById(R.id.avatar),
                tbackground = findViewById(R.id.background);
        TextView tname = findViewById(R.id.username),
                tdescription = findViewById(R.id.description),tstatus = findViewById(R.id.tstatus) ;

        Button tbtnfriend = findViewById(R.id.btnfriend),
                tbtnblock = findViewById(R.id.btnblock);
        if (targetuid == null || targetuid.isEmpty()) {
            finish();
        }
        WallViewModel wallViewModel = new ViewModelProvider(this).get(WallViewModel.class);
        wallViewModel.loadData(targetuid, currentuid);
        wallViewModel.getName().observe(this, name -> {
            tname.setText(name);
        });
        wallViewModel.getAvatar().observe(this, avatar -> {
            Glide.with(this)
                    .load(avatar)
                    .placeholder(R.drawable.avartarerror)
                    .error(R.drawable.avartarerror)
                    .into(tavatar);
        });
            // Load avatar into ImageView (not shown in this snippet)
        wallViewModel.getBackground().observe(this, background -> {
            if (background != null && !background.isEmpty()) {
                // Load background into ImageView (not shown in this snippet)
                Glide.with(this)
                        .load(background)
                        .placeholder(R.drawable.avartarerror)
                        .error(R.drawable.avartarerror)
                        .into(tbackground);
            }});
        wallViewModel.getDes().observe(this, des -> {
            if (des != null && !des.isEmpty()) {
                tdescription.setText(des);
                // Set description in TextView (not shown in this snippet)
            }
        });
        wallViewModel.getStatus().observe(this, status -> {
            if (status==-2){tstatus.setText("Trang của tôi");}
            else if (status==-1){tstatus.setText("Không có trạng thái");
            }
            else if (status==0){tstatus.setText("Chưa kết bạn");}
            else if (status==1){tstatus.setText("Đã kết bạn");}
            else if (status==2){tstatus.setText("Đã gửi lời mời kết bạn");}
            else if (status==3){tstatus.setText("Đã chặn");}
         });


    }}