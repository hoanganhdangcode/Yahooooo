package com.hoanganhdangcode.yahooooo.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hoanganhdangcode.yahooooo.Model.UserData;
import com.hoanganhdangcode.yahooooo.Model.UserLogin;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

import java.util.Calendar;


public class SignupActivity extends AppCompatActivity {
    EditText ename, ebirth, ephone, epassword, econfirmpassword;
    String name, birth, phone, password, confirmpassword;
    int gender;
    RadioGroup egender;
    Button btnsignup;
    ImageButton show1,show2;
    int showed1=0,showed2=0;
    int trytime=4;


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




//nut dang ki, lay du lieu, kiem tra du lieu, them vao database neu phone chua ton tai
        btnsignup.setOnClickListener(v->{
          getdata();
          if (checkdata()){
              String uid = Utils.genuuid();
              String hashphone = UtilsCrypto.md5(phone);
              String salt = Utils.genSalt();
              String hashpass = UtilsCrypto.md5(password+salt);

              UtilsDB.check("userlogin",hashphone,new UtilsDB.CheckCallback(){

                  @Override
                  public  void onSuccess(String userid) {
                      if (userid != null) {
                          Utils.noti(SignupActivity.this, "Số điện thoại đã tồn tại, bạn còn " + --trytime + " lần thử");
                          ephone.setError("Số điện thoại đã tồn tại");
                          if (trytime <= 0) {
                              Utils.noti(SignupActivity.this, "Quá nhiều lần thử, vui lòng thử lại sau");
                              System.exit(0);
                          }
                      }
                           else {


                                  //them du lieu vao db

                                  //them user login

                                                  UtilsDB.create("userlogin", hashphone, new UserLogin(uid, hashphone, hashpass,salt), new UtilsDB.CreateCallback() {
                                      @Override
                                      public void onSuccess() {
                                          UtilsDB.create("userdata", uid, new UserData(uid, name, gender, birth, "", "", "", 0, 0, 1), new UtilsDB.CreateCallback() {
                                              @Override
                                              public void onSuccess() {
                                                  Utils.noti(SignupActivity.this, "Đăng kí thành công!");
                                                  Intent i = new Intent(SignupActivity.this, SigninActivity.class);
                                                  startActivity(i);
                                                  finish();

                                              }

                                              @Override
                                              public void onFailure(Exception e) {

                                                  Utils.noti(SignupActivity.this, "Lỗi: " + e.getMessage());
                                              }


                                          });

                                      }

                                      @Override
                                      public void onFailure(Exception e) {
                                          Utils.noti(SignupActivity.this, "Lỗi: " + e.getMessage());

                                      }
                                  }
                          );//==============xong them user login=======================


                            }
                  }
                      @Override
                      public void onFailure (Exception e){
                          Utils.noti(SignupActivity.this, "Lỗi: " + e.getMessage());
                      }

                  });



          }

        });

    }






    public void initview() {
        ename=findViewById(R.id.ename);
        ebirth=findViewById(R.id.ebirth);
        // show dialog chon ngay sinh
        ebirth.setOnTouchListener((v,event)->{
            if(event.getAction()== MotionEvent.ACTION_DOWN){showdatepicker();}

            return true;

        });
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
        if (!regexphone(phone)){
            ephone.setError("Số điện thoại không hợp lệ");
            kt= false;
        }
        if (!regexbirth(birth)){
            ebirth.setError("Ngày sinh không hợp lệ");
            kt= false;

        }
        if (!regexpassword(password)){
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
    public boolean regexphone(String s) {
        String regex = "^(03|05|07|08|09)[0-9]{8}$";
        return s.matches(regex);
    }
    public boolean regexbirth(String s) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/[0-9]{4}$";
        return s.matches(regex);
    }
    public boolean regexpassword(String s) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return s.matches(regex);
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




}