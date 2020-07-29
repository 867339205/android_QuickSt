package com.quickst.main;

public class Video {
    private int id;
    private String path;
    private long size;
    private String displayName;
    private String title;
    private long time;

    private String imagePath;
    private long imageWidth;
    private long imageHeight;


    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageWidth(long imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(long imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTitle() {
        return title;
    }

    public long getTime() {
        return time;
    }

    public String getImagePath() {
        return imagePath;
    }

    public long getImageWidth() {
        return imageWidth;
    }

    public long getImageHeight() {
        return imageHeight;
    }
}
