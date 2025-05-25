package com.hoanganhdangcode.yahooooo.Model;

public class UserData {
    String uid;
    String name;
    String phone;
    int gender;
    String birth;
    String avatar;
    String background;
    String description;
    int status;
    long lastseentime;
    int permission;

    public UserData() {
    }

    public UserData(String uid, String name, String phone, int gender, String birth, String avatar, String background, String description, int status, long lastseentime, int permission) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.birth = birth;
        this.avatar = avatar;
        this.background = background;
        this.description = description;
        this.status = status;
        this.lastseentime = lastseentime;
        this.permission = permission;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getLastseentime() {
        return lastseentime;
    }

    public void setLastseentime(long lastseentime) {
        this.lastseentime = lastseentime;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}