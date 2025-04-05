package com.hoanganhdangcode.yahooooo.Model;

public class OtpRequest {
    String otpid;
    String phone;

    int status;


    public OtpRequest(String otpid, String phone, int status) {
        this.otpid = otpid;
        this.phone = phone;
        this.status = status;
    }

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


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
