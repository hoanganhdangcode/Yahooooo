package com.hoanganhdangcode.yahooooo.Model;

public class FriendDisplay {
    String uid;
    String name;
    String phone;
    String avatar;
    int status;
    long timestamp;

    public FriendDisplay() {
    }

    public FriendDisplay(String uid, String name, String phone, String avatar, int status, long timestamp) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
