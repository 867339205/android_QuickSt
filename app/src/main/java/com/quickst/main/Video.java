package com.quickst.main;

public class Video {
    private int imageId;
    private String title;

    public Video(int imageId,String title){
        this.imageId=imageId;
        this.title=title;
    }

    public int getImageId(){
        return imageId;
    }
    public String getTitle(){
        return title;
    }
}
