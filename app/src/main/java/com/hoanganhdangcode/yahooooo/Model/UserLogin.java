package com.hoanganhdangcode.yahooooo.Model;

public class UserLogin {
    String uid;
    String hashphone;
    String hashpass;
    String salt;

    public UserLogin(String uid, String hashphone, String hashpass,String salt) {
        this.uid = uid;
        this.hashphone = hashphone;
        this.hashpass = hashpass;
        this.salt=salt;
    }
    public UserLogin() {
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHashphone() {
        return hashphone;
    }

    public void setHashphone(String hashphone) {
        this.hashphone = hashphone;
    }

    public String getHashpass() {
        return hashpass;
    }

    public void setHashpass(String hashpass) {
        this.hashpass = hashpass;
    }
}