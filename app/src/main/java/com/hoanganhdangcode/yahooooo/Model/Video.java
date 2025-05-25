package com.hoanganhdangcode.yahooooo.Model;

public class Video {
    String path;
    String thumbnail;

    public Video() {
    }

    public Video(String path, String thumbnail) {
        this.path = path;
        this.thumbnail = thumbnail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
