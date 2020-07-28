package com.quickst.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

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

                //sleep(1000);
                List<Video> mVideoList=new ArrayList<Video>();

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
                        MediaStore.Video.Media.DISPLAY_NAME+" desc");
                if(mCursor!=null&&mCursor.moveToFirst()) {
                    do{

                        // 获取视频的路径
                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                        int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb

                        if (size < 0) {
                            //某些设备获取size<0，直接计算
                            Log.e("dml", "this video size < 0 " + path);
                            size = new File(path).length() / 1024;
                        }
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                        MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                        String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                        Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                                , projection
                                , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                                , new String[]{videoId + ""}
                                , null);
                        String thumbPath = "";
                        while (cursor.moveToNext()) {

                            thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        }
                        cursor.close();

                        Log.e("ceshi",name);
                    }
                    while (mCursor.moveToNext());
                    mCursor.close();

                }
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
                        if(getView()!=null){

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
