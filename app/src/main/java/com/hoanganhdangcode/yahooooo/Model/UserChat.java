package com.hoanganhdangcode.yahooooo.Model;

public class UserChat {
    String uid1;
    String chatid;
    String uid2;
    String namechat;
    String avatarchat;
    boolean pin;
    boolean mute;

    public UserChat() {

    }

    public UserChat(String uid1, String chatid, String uid2, String namechat, String avatarchat, boolean pin, boolean mute) {
        this.uid1 = uid1;
        this.chatid = chatid;
        this.uid2 = uid2;
        this.namechat = namechat;
        this.avatarchat = avatarchat;
        this.pin = pin;
        this.mute = mute;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public String getNamechat() {
        return namechat;
    }

    public void setNamechat(String namechat) {
        this.namechat = namechat;
    }

    public String getAvatarchat() {
        return avatarchat;
    }

    public void setAvatarchat(String avatarchat) {
        this.avatarchat = avatarchat;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }
}



