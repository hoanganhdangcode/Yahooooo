package com.hoanganhdangcode.yahooooo.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hoanganhdangcode.yahooooo.Model.UserData;
import com.hoanganhdangcode.yahooooo.Model.UserLogin;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;

import com.hoanganhdangcode.yahooooo.ViewModel.AuthViewModel;

import java.util.Calendar;


public class SignupActivity extends AppCompatActivity {
    EditText ename, ebirth, ephone, epassword, econfirmpassword;
    String name, birth, phone, password, confirmpassword;
    int gender;
    RadioGroup egender;
    Button btnsignup;
    ProgressBar progressBar;
    ImageButton show1,show2,nightmode;
    boolean isnightmode;
    int showed1=0,showed2=0;
    int trytime=4;
    private AuthViewModel viewModel;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initview();
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.getSignupMessage().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
        viewModel.getSignupSuccess().observe(this, success -> {
            if (success) {
//                Intent i= new Intent(SignupActivity.this,HomeActivity.class);
//                Utils.savepref(SignupActivity.this,"logined","uid",viewModel.getLoginUid().getValue());
//                startActivity(i);
//                finish();
            }
        });
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnsignup.setText(isLoading?"":"Đăng kí");
            btnsignup.setEnabled(isLoading?false:true);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });









//nut dang ki, lay du lieu, kiem tra du lieu, them vao database neu phone chua ton tai
        btnsignup.setOnClickListener(v->{
          getdata();
          if (checkdata()){
              viewModel.signup(name,gender,birth,phone,password);
          }

        });

    }






    public void initview() {
        settingnightmode();
        ename=findViewById(R.id.ename);
        progressBar = findViewById(R.id.progressbar);
        ebirth=findViewById(R.id.ebirth);
        // show dialog chon ngay sinh

        ebirth.setOnTouchListener((v,event)->{
            if(event.getAction()== MotionEvent.ACTION_DOWN){showdatepicker();}

            return true;

        });
        ebirth.setOnFocusChangeListener(
                (v, hasFocus) -> {if (hasFocus)showdatepicker();}
        );
        ephone=findViewById(R.id.ephone);
        epassword=findViewById(R.id.epassword);
        econfirmpassword=findViewById(R.id.econfirmpassword);
        egender=findViewById(R.id.egender);
        btnsignup=findViewById(R.id.btnsignup);
        show1 = findViewById(R.id.show1);
        show2 = findViewById(R.id.show2);
        ///hien thi mata khau / che mat khau
        show1.setOnClickListener(v->{
            if (showed1++%2==0){
                Utils.showpass(epassword);
            }
            else
            {
                Utils.hiddenpass(epassword);
            }
        });
        show2.setOnClickListener(v->{
            if (showed2++%2==0){
                Utils.showpass(econfirmpassword);
            }
            else
            {
                Utils.hiddenpass(econfirmpassword);
            }
        });


    }
    public void getdata() {
        name=ename.getText().toString();
        birth=ebirth.getText().toString();
        phone=ephone.getText().toString();
        password=epassword.getText().toString();
        confirmpassword=econfirmpassword.getText().toString();
        if(egender.getCheckedRadioButtonId()==R.id.male){
            gender=1;
        }else{
            gender=0;
        }

    }
    public boolean checkdata(){
        boolean kt=true;
        if(name.equals("")){
            ename.setError("Vui lòng nhập tên");
            kt= false;
        }
        if(birth.equals("")){
            ebirth.setError("Vui lòng nhập ngày sinh");
            kt= false;
        }
        if (phone.equals("")){
            ephone.setError("Vui lòng nhập số điện thoại");
            kt= false;
        }
        if(password.equals("")){
            epassword.setError("Vui lòng nhập mật khẩu");
            kt= false;

        }
        if(confirmpassword.equals("")){
            econfirmpassword.setError("Vui lòng nhập lại mật khẩu");
            kt= false;

        }
        if (!Utils.regexphone(phone)){
            ephone.setError("Số điện thoại không hợp lệ");
            kt= false;
        }
        if (!Utils.regexbirth(birth)){
            ebirth.setError("Ngày sinh không hợp lệ");
            kt= false;

        }
        if (!Utils.regexpassword(password)){
            epassword.setError("Mật khẩu ít nhất 8 kí tự, ít nhất 1 số, 1 chữ");
            kt= false;
        }
        if (!confirmpassword.equals(password)){
            econfirmpassword.setError("Mật khẩu không khớp");
            kt= false;

        }
        if (!eged(birth)){
            ebirth.setError("Bạn chưa đủ 16 tuổi");
            kt= false;
        }

        return kt;
    }
   
   
    public void showdatepicker() {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Cập nhật TextView khi chọn ngày
                    String ngaySinh = selectedDay + "/" ;
                    if (selectedMonth<10){ngaySinh+="0";}
                    ngaySinh += (selectedMonth + 1) + "/" + selectedYear;
                    if (selectedDay<10){ngaySinh="0"+ngaySinh;}
                    ebirth.setText(ngaySinh);
                },
                year, month, day
        );
        datePickerDialog.show();

    }
    public boolean eged(String birth){
        if (!birth.equals("")){
            int i=0;
        try {
             i = Integer.parseInt(birth.substring(6));
        }catch (Exception e){
        }
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            if (year<i+16) {return false; }else {return true;}
        }
        return true;
    }
    public void settingnightmode(){
        nightmode = findViewById(R.id.nightmode);
        SharedPreferences sharedPreferences = getSharedPreferences("nightmode", MODE_PRIVATE);
        isnightmode = sharedPreferences.getBoolean("nightmode", false);
        if (isnightmode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightmode.setImageResource(R.drawable.sunset);
        } else{                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightmode.setImageResource(R.drawable.night);
        }

        nightmode.setOnClickListener(v->{
            isnightmode = !isnightmode;
            SharedPreferences preferences = getSharedPreferences("nightmode", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("nightmode", isnightmode);
            editor.apply();
            if (isnightmode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nightmode.setImageResource(R.drawable.sunset);
            } else{ AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                nightmode.setImageResource(R.drawable.night);
            }
        });
    }



}