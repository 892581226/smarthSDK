package com.example.smarthome.iot.impl.device.cateye.activity;

import android.content.Context;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.utils.ELog;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.cateye.entity.AlarmMessageInfo;
import com.example.smarthome.iot.impl.device.cateye.utils.ImageLoaderThreadPool;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ouhuang on 2017/7/28.
 */

public class AlarmListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<AlarmMessageInfo> infos;
    private ICVSSUserInstance icvss;
    private ImageLoaderThreadPool imageLoaderThreadPool;

    public AlarmListAdapter(Context ctx, ArrayList<AlarmMessageInfo> infos, ICVSSUserInstance icvss){
        this.mContext = ctx;
        this.infos = infos;
        this.icvss = icvss;
        mInflater = LayoutInflater.from(ctx);
        imageLoaderThreadPool = ImageLoaderThreadPool.getInstance(3, ImageLoaderThreadPool.Type.LIFO);
    }

    /**
     * 刷新
     * @param refershData
     */
    public void onRefersh(ArrayList<AlarmMessageInfo> refershData){
        ELog.i("AlarmListActivity, onRefersh start------->");
        this.infos = refershData;
    }

    public int getCount() {
        return infos.size();
    }

    public Object getItem(int i) {
        if(infos != null && !infos.isEmpty()){
            return infos.get(i);
        }else{
            return null;
        }
    }

    public long getItemId(int i) {
        return i;
    }

    public static class ViewHolder {
        ImageView iv_alarmImage;
        TextView tv_alarmTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_alarmlist, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_alarmImage = (ImageView) convertView.findViewById(R.id.iv_alarmImage);
            viewHolder.tv_alarmTime = (TextView) convertView.findViewById(R.id.tv_alarmTime);
            convertView.setTag(viewHolder);

            int windowWidth = getScreenWidth(mContext);
            int width = windowWidth / 3;
            int height = (width * 3) / 4;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            viewHolder.iv_alarmImage.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AlarmMessageInfo info = (AlarmMessageInfo) getItem(position);

        if(info != null){
            if(info.getAlarmTime() > 0){
                viewHolder.tv_alarmTime.setText(getCurrentDateTimeString(info.getAlarmTime()));
            }

            List<String> pvids = info.getPvids();
            if(!pvids.isEmpty()){
                String imageUrlStr = null;
                String imagePathTemp = null;
                
                if (StringUtils.isNotBlank(pvids.get(0)) && StringUtils.isNotBlank(info.getBid())) {
                    imageUrlStr = icvss.equesGetThumbUrl(pvids.get(0), info.getBid()).toString();
                    imagePathTemp = getAlarmPath() + pvids.get(0);
                }
                ELog.i("alarmList", "imageUrlStr: ", imageUrlStr);
                ELog.i("alarmList", "imagePathTemp: ", imagePathTemp);
                
                viewHolder.iv_alarmImage.setTag(imagePathTemp);
                if(StringUtils.isNotBlank(imageUrlStr) && StringUtils.isNotBlank(imagePathTemp)){
                    imageLoaderThreadPool.loadImage(imageUrlStr, viewHolder.iv_alarmImage, imagePathTemp, imagePathTemp);     
                }else{
//                    viewHolder.iv_alarmImage.setImageResource(R.drawable.image_empty_photo);
                }
            }
        }
        return convertView;
    }

    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public String getCurrentDateTimeString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }
    
    public boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/";
        }
    }
    
    public String getAlarmPath() {
        String camPicPath = getRootFilePath() + "com.equessdk.app" + File.separator + "alarm_image" + File.separator;
        return camPicPath;
    }
}
