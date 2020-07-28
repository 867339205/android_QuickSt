package com.quickst.main;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.quickst.R;
import com.quickst.baseClass.mvp.baseActivity;
import com.quickst.mComponent.loadingView;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends baseActivity<mainModel, mainView, mainPresenter> implements mainView {

    //组件
    private ListView listView;
    private CircleRefreshLayout refresh_layout;
    private loadingView loading;

    //变量
    private videoListAdapter adapter;
    private List<Video> mVideoList=new ArrayList<Video>();

    //提供UI操作
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.e("CESHI:","收到msg"+msg.what);
            switch (msg.what)
            {

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mVideoList.add(new Video(1,"123"));

        initView();
        initEvent();
        initList();
        loading.setVisibility(View.GONE);
        refresh_layout.setVisibility(View.VISIBLE);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // 外部类Activity生命周期结束时，同时清空消息队列 & 结束Handler生命周期
        mHandler.removeCallbacksAndMessages(null);
    }

    private void initView(){
     listView=findViewById(R.id.video_list);
     refresh_layout=findViewById(R.id.refresh_layout);
     loading=findViewById(R.id.loading);


        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }
    private void initEvent(){

        refresh_layout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        // do something when refresh starts

                    }

                    @Override
                    public void completeRefresh() {
                        // do something when refresh complete
                    }
                });
        refresh_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_layout.finishRefreshing();
            }
        });
    }
    private void initList(){
        adapter=new videoListAdapter(this,R.layout.video_list,mVideoList);
        listView.setAdapter(adapter);

        if(presenter!=null){
            presenter.getVideoList(this);
        }
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


    @Override
    public void getVideoListTurn(List<Video> video) {
        mVideoList.clear();
        mVideoList.addAll(video);
        adapter.notifyDataSetChanged();
    }
}
