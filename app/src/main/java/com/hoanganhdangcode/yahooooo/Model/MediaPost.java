package com.hoanganhdangcode.yahooooo.Model;

public class MediaPost {
    private String url;
    private String localUri;
    private int status;

    public MediaPost() {}

    public MediaPost(String url, String localUri, int status) {
        this.url = url;
        this.localUri = localUri;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}