package com.hoanganhdangcode.yahooooo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hoanganhdangcode.yahooooo.Model.OtpRequest;
import com.hoanganhdangcode.yahooooo.Model.Otpdata;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;
import com.hoanganhdangcode.yahooooo.Util.UtilsDB;

public class OtpSubmitActivity extends AppCompatActivity {
    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;
    private EditText otp5;
    private EditText otp6;
    private Button btnxacnhan;
    private Button btnguilai;
    String otpsummit;
    String otpid;
    int time=6000;

    String phone;
    int trytime=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_submit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        initview();

    }
    public void initview(){
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        btnxacnhan = findViewById(R.id.btnxacnhan);
        btnguilai=findViewById(R.id.btnguilai);



        otp1.requestFocus();
        InputMethodManager imm = (InputMethodManager) otp1.getContext().getSystemService(OtpSubmitActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(otp1, InputMethodManager.SHOW_IMPLICIT);


        otp1.addTextChangedListener(new OTPTextWatcher(otp1, otp2, otp1));
        otp2.addTextChangedListener(new OTPTextWatcher(otp2, otp3, otp1));
        otp3.addTextChangedListener(new OTPTextWatcher(otp3, otp4, otp2));
        otp4.addTextChangedListener(new OTPTextWatcher(otp4, otp5, otp3));
        otp5.addTextChangedListener(new OTPTextWatcher(otp5, otp6, otp4));
        otp6.addTextChangedListener(new OTPTextWatcher(otp6, otp6, otp5));

        phone=getIntent().getStringExtra("phone");
        guiotp();
        btnxacnhan.setOnClickListener(v->{
            getdata();
            if (checkdata()){
                UtilsDB.read("otp", otpid, Otpdata.class, new UtilsDB.ReadCallback<Otpdata>() {
                    @Override
                    public  void onSuccess(Otpdata result) {

                        Otpdata otpdata =  result;
                        String hashotp = UtilsCrypto.md5(otpsummit);
                            if (!otpdata.getOtp().equals(hashotp)) {
                            Utils.noti(OtpSubmitActivity.this, "Lỗi: mã OTP không đúng");

                        } else if (otpdata.getStatus()>=1) {
                            Utils.noti(OtpSubmitActivity.this, "Lỗi: mã OTP không hợp lệ");

                        }
                        else if (System.currentTimeMillis() > otpdata.getTimestamp()+50000) {
                            Utils.noti(OtpSubmitActivity.this, "Lỗi: mã OTP đã hết hạn");
                        } else {
                            Utils.noti(OtpSubmitActivity.this,"Thành công");
                            Intent i1 = new Intent(OtpSubmitActivity.this, HomeActivity.class);
                            startActivity(i1);
                            finish();
                        }

                    }
                    @Override
                    public void onNotFound(){
                        Utils.noti(OtpSubmitActivity.this, "Lỗi: mã OTP không tồn tại");
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });


            }
        });
                //xac nhan otp

        btnguilai.setOnClickListener(v->{
            guiotp();
            time+=time;
            btnguilai.setEnabled(false);

            btnguilai.setBackgroundColor(getResources().getColor(R.color.grey));

            new CountDownTimer(time, 1000) { // 5 giây, cập nhật mỗi giây
                public void onTick(long millisUntilFinished) {
                    btnguilai.setText("Chờ " + millisUntilFinished / 1000 + "s");
                }
                public void onFinish() {
                    btnguilai.setText("Gửi lại OTP");
                    btnguilai.setEnabled(true); // Bật lại nút
                    btnguilai.setBackgroundColor(getResources().getColor(R.color.df));
                }
            }.start();


        });
    }
    public class OTPTextWatcher implements TextWatcher {

        private EditText current, next, previous;

        // Constructor: nhận vào các EditText hiện tại, tiếp theo và trước đó
        public OTPTextWatcher(EditText current, EditText next, EditText previous) {
            this.current = current;
            this.next = next;
            this.previous = previous;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // Không cần xử lý gì ở đây
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // Kiểm tra nếu người dùng đã nhập ký tự
            if (current.getText().length() == 1 ) {
                next.requestFocus();  // Chuyển focus sang ô tiếp theo
            } else if (current.getText().length() == 0 ) {
                previous.requestFocus();  // Chuyển focus sang ô trước đó nếu người dùng xóa ký tự
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }
    public void getdata(){

        otpsummit = otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString()+otp5.getText().toString()+otp6.getText().toString();
        Utils.noti(OtpSubmitActivity.this,"otpsubmit: "+otpsummit);
    }
    public boolean checkdata(){

        boolean kt=true;
        if (phone==null){kt=false;}
        if (otp1.equals("")){
            otp1.setError("Vui lòng nhập mã OTP");
            kt= false;
        }
        if (otp2.equals("")){
            otp2.setError("Vui lòng nhập mã OTP");
            kt= false;
        }
        if (otp3.equals("")){
            otp3.setError("Vui lòng nhập mã OTP");
            kt= false;
        }
        if (otp4.equals("")){
            otp4.setError("Vui lòng nhập mã OTP");
            kt= false;
        }
        if (otp5.equals("")){
            otp5.setError("Vui lòng nhập mã OTP");
            kt=false;
        }
        if (otp6.equals("")){
            otp6.setError("Vui lòng nhập mã OTP");
            kt=false;
        }
        if (otpsummit.length()!=6){
            kt=false;
            btnxacnhan.setError("Lỗi dữ liệu");
        }
        return kt;

    }
    public void guiotp(){
         otpid = Utils.genuuid();
        UtilsDB.create("otprequest",otpid, new OtpRequest(otpid,phone,0), new UtilsDB.CreateCallback(){
            @Override
            public void onSuccess() {
                Utils.noti(OtpSubmitActivity.this, "Đang gửi OTP đến số điện thoại "+phone+" vui lòng kiểm tra tin nhắn");
            }

            @Override
            public void onFailure(Exception e) {
                Utils.noti(OtpSubmitActivity.this, "Lỗi: "+e.getMessage());
            }
        });

    }
}