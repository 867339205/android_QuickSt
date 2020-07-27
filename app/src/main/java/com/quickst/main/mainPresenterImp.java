package com.quickst.main;

public class mainPresenterImp extends mainPresenter<mainModel, mainView>{
    public void onViewDestroy(){
        if (model != null) {
            model.stopRequest();
        }
    }
}
