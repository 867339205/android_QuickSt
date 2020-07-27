package com.quickst.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.quickst.R;
import com.quickst.baseClass.mvp.baseActivity;


public class MainActivity extends baseActivity<mainModel, mainView, mainPresenter> implements mainView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }














    ////////////////////////////实现///////////////////////////////////////
    public mainModel createModel(){
        return new mainModelImp();
    }
    public mainView createView(){
        return this;
    }
    public mainPresenter createPresenter(){
        return new mainPresenterImp();
    }

    @Override
    public void showToast(String info) {
        Toast.makeText(getApplicationContext(), ""+info, Toast.LENGTH_SHORT).show();
    }
}
