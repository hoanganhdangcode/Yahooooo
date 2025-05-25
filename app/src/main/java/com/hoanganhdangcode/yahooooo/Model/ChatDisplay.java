package com.hoanganhdangcode.yahooooo.Model;

public class ChatDisplay {
    String chatid;
    int type;
    String uid1;
    String uid2;
    String name;
    String avatar;
    String lastmessage;
    String userlast;
    long lasttimestamp;

    public ChatDisplay() {
    }

    public ChatDisplay(String chatid, int type, String uid1, String uid2, String name, String avatar, String lastmessage, String userlast, long lasttimestamp) {
        this.chatid = chatid;
        this.type = type;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.name = name;
        this.avatar = avatar;
        this.lastmessage = lastmessage;
        this.userlast = userlast;
        this.lasttimestamp = lasttimestamp;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getUserlast() {
        return userlast;
    }

    public void setUserlast(String userlast) {
        this.userlast = userlast;
    }

    public long getLasttimestamp() {
        return lasttimestamp;
    }

    public void setLasttimestamp(long lasttimestamp) {
        this.lasttimestamp = lasttimestamp;
    }
}

