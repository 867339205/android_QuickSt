package com.quickst.baseClass.mvp;

import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.quickst.baseClass.mModel.myModel;
import com.quickst.baseClass.mPresenter.basePresenter;
import com.quickst.baseClass.mView.myView;

public abstract class baseFragment<M extends myModel, V extends myView, P extends basePresenter> extends Fragment implements com.quickst.baseClass.mvp.baseMvp<M, V, P> {
    protected P presenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        presenter = createPresenter();
        if (presenter != null) {
            presenter.registerModel(createModel());
            presenter.registerView(createView());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.destroy();
        }
    }
}
