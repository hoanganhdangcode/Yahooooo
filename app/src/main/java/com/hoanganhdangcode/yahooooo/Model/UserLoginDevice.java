package com.hoanganhdangcode.yahooooo.Model;

public class UserLoginDevice {
    String RfToken;
    String DvInfo;
    String timestamp;

    public UserLoginDevice(String rfToken, String dvInfo, String timestamp) {
        RfToken = rfToken;
        DvInfo = dvInfo;
        this.timestamp = timestamp;
    }

    public String getRfToken() {
        return RfToken;
    }

    public void setRfToken(String rfToken) {
        RfToken = rfToken;
    }

    public String getDvInfo() {
        return DvInfo;
    }

    public void setDvInfo(String dvInfo) {
        DvInfo = dvInfo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
