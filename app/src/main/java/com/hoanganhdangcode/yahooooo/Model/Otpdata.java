package com.hoanganhdangcode.yahooooo.Model;

public class Otpdata {
    String otpid;
    String phone;
    String otp;
    long timestamp;
    int status;
    public Otpdata() {}

    public Otpdata(String otpid,String phone, String otp, long timestamp, int status) {
        this.otpid = otpid;
        this.phone = phone;
        this.otp = otp;
        this.timestamp = timestamp;
        this.status = status;}

    public String getOtpid() {
        return otpid;
    }

    public void setOtpid(String otpid) {
        this.otpid = otpid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
