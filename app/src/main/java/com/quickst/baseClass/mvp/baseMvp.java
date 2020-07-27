package com.quickst.baseClass.mvp;


import com.quickst.baseClass.mModel.myModel;
import com.quickst.baseClass.mPresenter.basePresenter;
import com.quickst.baseClass.mView.myView;

public interface baseMvp<M extends myModel, V extends myView, P extends basePresenter> {
    M createModel();

    V createView();

    P createPresenter();
}
