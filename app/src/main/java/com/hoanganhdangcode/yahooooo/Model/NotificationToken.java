package com.hoanganhdangcode.yahooooo.Model;

public class NotificationToken {
    String deviceid;
    String uid;
    String token;

    public NotificationToken(String deviceid, String uid, String token) {
        this.deviceid = deviceid;
        this.uid = uid;
        this.token = token;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
