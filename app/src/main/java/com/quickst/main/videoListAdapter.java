package com.quickst.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickst.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class videoListAdapter  extends BaseAdapter {
    private int resourceId;
    private Context mContext;
    private List<Video> data;

    public videoListAdapter(Context context,int textViewResourceId,List<Video> objects){
        mContext=context;
        data=objects;
        resourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){


        View view;
        ViewHolder viewHolder;
        Video video=data.get(position);
        if(convertView==null){
            view = LayoutInflater.from(mContext).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.video_row=view.findViewById(R.id.video_row);
            viewHolder.rowImage=(ImageView)view.findViewById(R.id.video_image);
            viewHolder.rowName=(TextView)view.findViewById(R.id.row_name);
            viewHolder.rowStart=view.findViewById(R.id.row_start) ;
            viewHolder.rowStartCycle=view.findViewById(R.id.row_start_cycle) ;
            view.setTag(viewHolder);

        }

        else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }

        initView(viewHolder,video);
        initEvent(viewHolder,video);


        return view;
    }

    private void initView(ViewHolder viewHolder,Video video){

        viewHolder.rowName.setText(video.getTitle());

    }
    private void initEvent(ViewHolder viewHolder,Video video){

        viewHolder.video_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HashMap<String,List<Video>> allPhotosTemp = new HashMap<>();//所有照片
                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] proj = { MediaStore.Video.Thumbnails._ID
                        , MediaStore.Video.Thumbnails.DATA//视频的绝对路径
                        ,MediaStore.Video.Media.DURATION//视频时长
                        ,MediaStore.Video.Media.SIZE//视频文件的大小
                        ,MediaStore.Video.Media.DISPLAY_NAME//视频在sd卡中的名称
                        ,MediaStore.Video.Media.DATE_MODIFIED};
                Cursor mCursor = mContext.getContentResolver().query(mImageUri,
                        proj,
                        MediaStore.Video.Media.MIME_TYPE + "=?",
                        new String[]{"video/mp4"},
                        MediaStore.Video.Media.DATE_MODIFIED+" desc");
                if(mCursor!=null) {
                    while (mCursor.moveToNext()) {
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
                        Log.e("ceshi", path+","+thumbPath+","+duration+","+size+","+displayName+",");
                    }
                    mCursor.close();
                }

            }
        });

    }

    class ViewHolder{
        LinearLayout video_row;
        ImageView rowImage;
        TextView rowName;
        Button rowStart;
        Button rowStartCycle;
    }


}
