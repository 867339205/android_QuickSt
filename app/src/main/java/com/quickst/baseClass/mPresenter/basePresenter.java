package com.quickst.baseClass.mPresenter;


import com.quickst.baseClass.mModel.myModel;
import com.quickst.baseClass.mView.myView;

import java.lang.ref.WeakReference;

public abstract  class basePresenter<M extends myModel, V extends myView> implements com.quickst.baseClass.mPresenter.myPresenter<M, V> {

    /**
     * 使用弱引用来防止内存泄漏
     */
    private WeakReference<V> wrf;
    protected M model;

    @Override
    public void registerModel(M model) {
        this.model = model;
    }

    @Override
    public void registerView(V view) {
        wrf = new WeakReference<V>(view);
    }

    @Override
    public V getView() {
        return wrf == null ? null : wrf.get();
    }

    /**
     * 在Activity或Fragment卸载时调用View结束的方法
     */
    @Override
    public void destroy() {
        if (wrf != null) {
            wrf.clear();
            wrf = null;
        }
        onViewDestroy();
    }

    //可用于停止当前的model以及presenter的工作
    public abstract void onViewDestroy();
        /*if (model != null) {
            model.stopRequest();
        }*/

}
