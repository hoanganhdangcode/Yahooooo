package com.hoanganhdangcode.yahooooo.Model;

public class UserFriend {
    String uid1;
    String uid2;
    int status;
    long timestamp;

    public UserFriend(String uid1, String uid2, int status, long timestamp) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.status = status;
        this.timestamp = timestamp;
    }
    public UserFriend(){}

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
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

