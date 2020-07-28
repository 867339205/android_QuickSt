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
                List<Video> mVideoList=new ArrayList<Video>();

                HashMap<String,List<Video>> allPhotosTemp = new HashMap<>();//所有照片
                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] proj = { MediaStore.Video.Thumbnails._ID
                        , MediaStore.Video.Thumbnails.DATA
                        ,MediaStore.Video.Media.DURATION
                        ,MediaStore.Video.Media.SIZE
                        ,MediaStore.Video.Media.DISPLAY_NAME
                        ,MediaStore.Video.Media.DATE_MODIFIED};
                Cursor mCursor = mContext.getContentResolver().query(mImageUri,
                        proj,
                        MediaStore.Video.Media.MIME_TYPE + "=?",
                        new String[]{"video/mp4"},
                        MediaStore.Video.Media.DATE_MODIFIED+" desc");
                Log.e("ceshi","go");
                if(mCursor!=null) {
                    Log.e("ceshi","go1");
                    while (mCursor.moveToNext()) {
                        Log.e("ceshi","go2");
                        // 获取视频的路径
                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb
                        if (size < 0) {
                            //某些设备获取size<0，直接计算
                            Log.e("dml", "this video size < 0 " + path);
                            size = new File(path).length() / 1024;
                        }
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到

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
                        // 获取该视频的父路径名
                        String dirPath = new File(path).getParentFile().getAbsolutePath();
                        //存储对应关系
                        if (allPhotosTemp.containsKey(dirPath)) {
                            //List<Video> data = allPhotosTemp.get(dirPath);
                            //data.add(new MediaBean(MediaBean.Type.Video,path,thumbPath,duration,size,displayName));
                            continue;
                        } else {
                           // List<Video> data = new ArrayList<>();
                            //data.add(new MediaBean(MediaBean.Type.Video,path,thumbPath,duration,size,displayName));
                           // allPhotosTemp.put(dirPath, data);
                        }
                        Log.e("ceshi",path);
                    }
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
