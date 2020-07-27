package com.quickst.baseClass.mPresenter;


import com.quickst.baseClass.mModel.myModel;
import com.quickst.baseClass.mView.myView;

//所有presenter超类
public interface myPresenter <M extends myModel, V extends myView>{
    /**
     * 注册Model层
     *
     * @param model
     */
    void registerModel(M model);

    /**
     * 注册View层
     *
     * @param view
     */
    void registerView(V view);

    /**
     * 获取View
     *
     * @return
     */
    V getView();

    /**
     * 销毁动作（如Activity、Fragment的卸载）
     */
    void destroy();

}
