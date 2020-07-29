package com.quickst.main;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.quickst.R;
import com.quickst.baseClass.mvp.baseActivity;
import com.quickst.mComponent.loadingView;
import com.quickst.mGlobal.mEntity.admin;
import com.quickst.tool.md5;
import com.quickst.tool.sharedPreferences;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends baseActivity<mainModel, mainView, mainPresenter> implements mainView {
   private final int SHOW_TOAST_MSG=0;

    //组件
    private ListView listView;
    private CircleRefreshLayout refresh_layout;
    private loadingView loading;

    //变量
    private videoListAdapter adapter;
    private List<Video> mVideoList= admin.getAdmin().getVideoList();

    //提供UI操作
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.e("CESHI:","收到msg"+msg.what);
            switch (msg.what)
            {
                case SHOW_TOAST_MSG:
                    showToast((String)msg.obj);
                    break;
            }
        }
    };

    //获取usb的连接信号
    private BroadcastReceiver mUsbStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*Message msg = Message.obtain();
            msg.what=SHOW_TOAST_MSG;
            msg.obj="onReceive: " + intent.getExtras().getBoolean("connected");
            mHandler.sendMessage(msg);*/
            admin.getAdmin().setUsbStatus(intent.getExtras().getBoolean("connected"));
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        initBroadcast();
        initView();
        initEvent();
        initList();
        initPass();//检测秘钥

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // 外部类Activity生命周期结束时，同时清空消息队列 & 结束Handler生命周期
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mUsbStateChangeReceiver);//解绑usb监听
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
                        if(presenter!=null){
                            presenter.getVideoList(MainActivity.this);
                        }
                    }

                    @Override
                    public void completeRefresh() {
                        // do something when refresh complete
                    }
                });
        refresh_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void initList(){
        adapter=new videoListAdapter(this,R.layout.video_list,mVideoList);
        listView.setAdapter(adapter);
        detectUsbWithBroadcast();
        if(presenter!=null){
            presenter.getVideoList(this);
        }
    }
    private void initBroadcast(){
        detectUsbWithBroadcast();//usb状态改变监听
    }

    private void detectUsbWithBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_STATE");
        registerReceiver(mUsbStateChangeReceiver, filter);
    }


    private void initPass(){
        final sharedPreferences mSharedPreferences=new sharedPreferences(this);
        if(md5.getMD5(mSharedPreferences.getPass()).equals(admin.password)){
            //Log.e("ceshi","相同");
            return;

        }
        else{
            //Log.e("ceshi","不相同");
            final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(this).builder()
                    .setTitle("软件许可验证")
                    .setEditText("请输入密码");
            myAlertInputDialog.setCanceledOnTouchOutside(false);
            myAlertInputDialog.setCancelable(false);
            myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myAlertInputDialog.getResult().equals("")||myAlertInputDialog.getResult()==null){
                        showToast("输入密码不能为空！");
                    }
                    else{
                        String pass=myAlertInputDialog.getResult();
                        if(md5.getMD5(pass).equals(admin.password)){
                            showToast("密码有效！");
                            mSharedPreferences.savePass(pass);
                            myAlertInputDialog.dismiss();
                        }
                        else{
                            showToast("密码无效！");
                        }
                       // Log.e("ceshi",md5.getMD5(pass));

                    }

                }
            });
            myAlertInputDialog.show();
        }
    }



    ///////提示按两次返回键退出程序////////////
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            System.exit(0);
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
    public void getVideoListTurn() {
        adapter.notifyDataSetChanged();
        loading.setVisibility(View.GONE);
        refresh_layout.setVisibility(View.VISIBLE);
        refresh_layout.finishRefreshing();
    }


}
