package com.hoanganhdangcode.yahooooo.Model;

public class Message {
    String chatid;
    String messageid;
    String sender;
    String content;
    int type;
    int status;
    long timestamp;
    String reply;
    int reaction;
    public Message() {}

    public Message(String chatid, String messageid, String sender, String content, int type, int status, long timestamp, String reply, int reaction) {
        this.chatid = chatid;
        this.messageid = messageid;
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.reply = reply;
        this.reaction = reaction;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
