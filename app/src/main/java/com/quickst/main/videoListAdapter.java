package com.quickst.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
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
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.quickst.R;
import com.quickst.mComponent.mActionSheetDialog;
import com.quickst.mGlobal.mEntity.admin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class videoListAdapter  extends BaseAdapter {
    private final String TAG="videoRow";
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

        if(!(new File(video.getImagePath()).exists())){
            MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), video.getId(), MediaStore.Video.Thumbnails.MINI_KIND, null);
        }
        if((new File(video.getImagePath()).exists())){
            viewHolder.rowImage.setImageURI(Uri.parse(video.getImagePath()));
        }





    }
    private void initEvent(final ViewHolder viewHolder, final Video video){

        viewHolder.video_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        viewHolder.video_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new mActionSheetDialog(mContext).builder()
                        .setTitle(video.getTitle())
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        /*.addSheetItem("重命名", mActionSheetDialog.SheetItemColor.BLACK,
                                new mActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {

                                    }
                                }
                        )*/
                         .addSheetItem("删除视频", mActionSheetDialog.SheetItemColor.Red,
                                 new mActionSheetDialog.OnSheetItemClickListener() {
                                     @Override
                                     public void onClick(int which) {
                                      File file=new File(video.getPath());
                                      if(file.isFile()&&file.exists()){
                                          if(file.delete()){
                                              admin.getAdmin().getVideoList().remove(video);
                                          }
                                      }
                                     }
                                 }
                         )
                         .show();
                return false;
            }
        });

        viewHolder.rowStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!admin.getAdmin().getUsbStatus()){
                    Toast.makeText(mContext.getApplicationContext(), "请先连接USB设备！", Toast.LENGTH_SHORT).show();
                    return;
                }
               videoStart(video);
            }
        });

    }

    private void videoStart(Video video){
        int videoTrackIndex = 0;//视频轨道索引
        MediaFormat videoMediaFormat = null;//视频格式

        File mFile = new File(video.getPath());
        if (!mFile.exists()){
            Log.e(TAG, "mp4文件不存在");
            return;
        }

        //初始化usb
        UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (!availableDrivers.isEmpty()) {
            Log.e(TAG, "usb数:"+availableDrivers.size());

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection != null) {
                UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                    //初始化视频
                    MediaExtractor extractor = new MediaExtractor();//实例一个MediaExtractor
                    extractor.setDataSource(mFile.getAbsolutePath());//设置添加MP4文件路径

                    int count = extractor.getTrackCount();//获取轨道数量
                    for (int i = 0; i < count; i++){
                        MediaFormat mediaFormat = extractor.getTrackFormat(i);
                        String itemMime = mediaFormat.getString(MediaFormat.KEY_MIME);
                        if(itemMime.startsWith("video")){
                            videoTrackIndex = i;
                            videoMediaFormat = mediaFormat;
                            break;
                        }
                    }

                    /**
                     * 获取视频数据
                     */
                    int maxVideoBufferCount = videoMediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);//获取视频的输出缓存的最大大小
                    ByteBuffer videoByteBuffer = ByteBuffer.allocate(maxVideoBufferCount);
                    extractor.selectTrack(videoTrackIndex);//选择到视频轨道
                    int len = 0;
                    int num=0;//帧数

                    //输出视频到usb
                    while ((len = extractor.readSampleData(videoByteBuffer, 0)) != -1) {
                        byte[] bytes = new byte[len];
                        videoByteBuffer.get(bytes);//获取字节
                        //获取到数据进行相应的操作
                        //Log.e(TAG, Arrays.toString(bytes));
                        num++;
                        port.write(bytes,1000);


                        videoByteBuffer.clear();
                        extractor.advance();//预先加载后面的数据
                    }
                    Log.e(TAG, "帧数:"+num);
                    extractor.release();//释放资源

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    class ViewHolder{
        LinearLayout video_row;
        ImageView rowImage;
        TextView rowName;
        Button rowStart;
        Button rowStartCycle;
    }


}
