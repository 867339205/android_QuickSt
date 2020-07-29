package com.quickst.main;

import android.content.Context;

import com.quickst.baseClass.mModel.myModel;
import com.quickst.baseClass.mPresenter.basePresenter;
import com.quickst.baseClass.mView.myView;

public abstract class mainPresenter<M extends myModel,V extends myView> extends basePresenter<M,V> {

    /**
     * 获取本地视频
     */
    public abstract void getVideoList(Context mContext);


}
