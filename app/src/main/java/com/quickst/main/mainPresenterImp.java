package com.quickst.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.quickst.mGlobal.mEntity.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

public class mainPresenterImp extends mainPresenter<mainModel, mainView>{
    public void onViewDestroy(){
        if (model != null) {
            model.stopRequest();
        }
    }


    @Override
    public void getVideoList(final Context mContext) {

        Observable.create(new ObservableOnSubscribe<List<Video>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Video>> emitter) throws Exception {


                List<Video> mVideoList=admin.getAdmin().getVideoList();
                mVideoList.clear();

                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] sLocalVideoColumns  = {
                        MediaStore.Video.Media._ID, // 视频id
                        MediaStore.Video.Media.DATA, // 视频路径
                        MediaStore.Video.Media.SIZE, // 视频字节大小
                        MediaStore.Video.Media.DISPLAY_NAME, // 视频名称 xxx.mp4
                        MediaStore.Video.Media.TITLE, // 视频标题
                        MediaStore.Video.Media.DURATION, // 视频时长
                         };
                String[] sLocalVideoThumbnailColumns = {
                        MediaStore.Video.Thumbnails.DATA, // 视频缩略图路径
                        MediaStore.Video.Thumbnails.VIDEO_ID, // 视频id
                        MediaStore.Video.Thumbnails.WIDTH, // 视频缩略图宽度
                        MediaStore.Video.Thumbnails.HEIGHT // 视频缩略图高度
                };

                Cursor mCursor = mContext.getContentResolver().query(mImageUri,
                        sLocalVideoColumns ,
                        MediaStore.Video.Media.MIME_TYPE + "=?",
                        new String[]{"video/mp4"},
                        MediaStore.Video.Media.DISPLAY_NAME+" asc");
                if(mCursor!=null&&mCursor.moveToFirst()) {
                    do{
                        Video mVideo=new Video();
                        // 获取视频的路径
                        mVideo.setId(mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID)));
                        mVideo.setPath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                        mVideo.setSize(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024);//单位kb
                        mVideo.setDisplayName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                        mVideo.setTitle(mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
                        mVideo.setTime(mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION)));

                        if (mVideo.getSize() < 0) {
                            //某些设备获取size<0，直接计算
                            mVideo.setSize(new File(mVideo.getPath()).length() / 1024);
                        }

                        MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), mVideo.getId(), MediaStore.Video.Thumbnails.MINI_KIND, null);
                        Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                                , sLocalVideoThumbnailColumns
                                , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                                , new String[]{mVideo.getId() + ""}
                                , null);

                        while (cursor.moveToNext()){
                                mVideo.setImagePath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                                mVideo.setImageWidth(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Thumbnails.WIDTH)));
                                mVideo.setImageHeight(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Thumbnails.HEIGHT)));
                            }



                        cursor.close();

                        mVideoList.add(mVideo);
                    }
                    while (mCursor.moveToNext());
                    mCursor.close();

                }
                sleep(1500);
                emitter.onNext(mVideoList);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Video>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Video> mVideoList) {
                        //Log.e("ceshi",""+mVideoList.size());
                        if(getView()!=null){
                         getView().getVideoListTurn();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
