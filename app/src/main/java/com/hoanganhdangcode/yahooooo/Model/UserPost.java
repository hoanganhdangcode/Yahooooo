package com.hoanganhdangcode.yahooooo.Model;

import java.util.List;

public class UserPost {
    String postid;
    String uid;
    int scope;
    String posttext;
    List<MediaPost> postlist;
    int status;
    long timestamp;
    int countreaction;
    int countcomment;
    public UserPost() {
    }

    public UserPost(String postid, String uid, int scope, String posttext, List<MediaPost> postlist, int status, long timestamp, int countreaction, int countcomment) {
        this.postid = postid;
        this.uid = uid;
        this.scope = scope;
        this.posttext = posttext;
        this.postlist = postlist;
        this.status = status;
        this.timestamp = timestamp;
        this.countreaction = countreaction;
        this.countcomment = countcomment;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public String getPosttext() {
        return posttext;
    }

    public void setPosttext(String posttext) {
        this.posttext = posttext;
    }

    public List<MediaPost> getPostlist() {
        return postlist;
    }

    public void setPostlist(List<MediaPost> postlist) {
        this.postlist = postlist;
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

    public int getCountreaction() {
        return countreaction;
    }

    public void setCountreaction(int countreaction) {
        this.countreaction = countreaction;
    }

    public int getCountcomment() {
        return countcomment;
    }

    public void setCountcomment(int countcomment) {
        this.countcomment = countcomment;
    }
}
