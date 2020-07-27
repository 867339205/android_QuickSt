package com.quickst.baseClass.mvp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.quickst.baseClass.mModel.myModel;
import com.quickst.baseClass.mPresenter.basePresenter;
import com.quickst.baseClass.mView.myView;

public abstract class baseActivity<M extends myModel, V extends myView, P extends basePresenter> extends AppCompatActivity implements com.quickst.baseClass.mvp.baseMvp<M, V, P> {
    protected P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建Presenter
        presenter = createPresenter();
        if (presenter != null) {
            //将Model层注册到Presenter中
            presenter.registerModel(createModel());
            //将View层注册到Presenter中
            presenter.registerView(createView());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            //Activity销毁时的调用，让具体实现BasePresenter中onViewDestroy()方法做出决定
            presenter.destroy();
        }
    }
}
