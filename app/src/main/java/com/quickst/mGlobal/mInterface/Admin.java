package com.quickst.mGlobal.mInterface;

import com.quickst.main.Video;

import java.util.List;

public interface Admin {
    String password="fd6b71718312e3cce773a6de5247694c";//QuickSt.2020.07.29

    /**
     * 获取视频列表
     * @return 视频列表
     */
    List<Video> getVideoList();


    /**
     * 获取usb状态
     * @return usb连接状态
     */
    Boolean getUsbStatus();

    /**
     * 设置usb状态
     * @param status 状态
     */
    void setUsbStatus(Boolean status);


}
