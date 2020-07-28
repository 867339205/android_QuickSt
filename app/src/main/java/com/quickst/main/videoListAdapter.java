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
