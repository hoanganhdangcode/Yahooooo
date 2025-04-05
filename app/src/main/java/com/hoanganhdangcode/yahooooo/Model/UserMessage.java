package com.hoanganhdangcode.yahooooo.Model;

public class UserMessage {
    String messid;
    String senderuid;
    String receiveruid;
    String content;
    int type;
    long time;
    int status;
    int reaction;

    public UserMessage(String messid, String senderuid, String receiveruid, String content, int type, long time, int status, int reaction) {
        this.messid = messid;
        this.senderuid = senderuid;
        this.receiveruid = receiveruid;
        this.content = content;
        this.type = type;
        this.time = time;
        this.status = status;
        this.reaction = reaction;
    }

    public String getMessid() {
        return messid;
    }

    public void setMessid(String messid) {
        this.messid = messid;
    }

    public String getSenderuid() {
        return senderuid;
    }

    public void setSenderuid(String senderuid) {
        this.senderuid = senderuid;
    }

    public String getReceiveruid() {
        return receiveruid;
    }

    public void setReceiveruid(String receiveruid) {
        this.receiveruid = receiveruid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
