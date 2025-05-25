package com.hoanganhdangcode.yahooooo.Model;

import java.util.List;

public class Chat {
    String chatid;
    int type;
    String lastmessage;
    String userlast;
    long lasttimestamp;
    public Chat() {

    }

    public Chat(String chatid, int type, String lastmessage, String userlast, long lasttimestamp) {
        this.chatid = chatid;
        this.type = type;
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
