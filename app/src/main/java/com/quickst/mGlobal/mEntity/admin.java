package com.quickst.mGlobal.mEntity;

import com.quickst.mGlobal.mInterface.Admin;
import com.quickst.main.Video;

import java.util.ArrayList;
import java.util.List;

public class admin implements Admin {
    private static Admin mAdmin;
    private List<Video> mVideoList=new ArrayList<Video>();
    private Boolean usbStatus=false;

    private admin(){}

    public static Admin getAdmin(){
        if(mAdmin==null){
            mAdmin=new admin();
        }
        return mAdmin;
    }


    @Override
    public List<Video> getVideoList() {
        return mVideoList;
    }

    @Override
    public Boolean getUsbStatus() {
        return usbStatus;
    }

    @Override
    public void setUsbStatus(Boolean status) {
        this.usbStatus=status;
    }
}
