package com.hoanganhdangcode.yahooooo.Model;

public class UserFriend {
    String senderid;
    String receiverid;
    int status;
    long timestamp;

    public UserFriend( String senderid, String receiverid, int status, long timeadd) {
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.status = status;
        this.timestamp = timeadd;

    }
    public UserFriend(){
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
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
