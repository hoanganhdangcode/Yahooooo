package com.hoanganhdangcode.yahooooo.Model;

import android.util.Patterns;

import com.hoanganhdangcode.yahooooo.Util.UtilsCrypto;

public class UserLogin {
    String uid;
    String phone;
    String hashpass;
    String salt;


    public UserLogin() {
    }

    public UserLogin(String uid, String phone, String hashpass, String salt) {
        this.uid = uid;
        this.phone = phone;
        this.hashpass = hashpass;
        this.salt = salt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHashpass() {
        return hashpass;
    }

    public void setHashpass(String hashpass) {
        this.hashpass = hashpass;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isValid(String phone, String pass){
        return !pass.isEmpty()&& !phone.isEmpty();
    }

    public boolean isValidPhone(String countryISO, String phone) {
        switch (countryISO) {
            case "VN": // Việt Nam
                return phone.matches("^(03|05|07|08|09)[0-9]{8}$");
            case "US": // Mỹ
                return phone.matches("^[2-9]{1}[0-9]{9}$"); // 10 số, không bắt đầu bằng 0/1
            case "IN": // Ấn Độ
                return phone.matches("^[6-9]\\d{9}$");
            default:
                return Patterns.PHONE.matcher(phone).matches(); // fallback
        }
    }


    public boolean isCorrect(String phone,String pass){
        return UtilsCrypto.md5(pass+this.salt).equals(hashpass)&& UtilsCrypto.md5(phone).equals(phone);
    }
}