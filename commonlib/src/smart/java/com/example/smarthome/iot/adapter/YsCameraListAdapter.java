//package com.example.smarthome.iot.adapter;
//
//import android.media.Image;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.BaseViewHolder;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.entry.RoomListVo;
//import com.example.smarthome.iot.yscamera.EZUtils;
//import com.videogo.openapi.bean.EZCameraInfo;
//import com.videogo.openapi.bean.EZDeviceInfo;
//
//import java.util.List;
//
///**
// * author:
// * date:
// * description: 萤石摄像头列表
// * update:
// * version:
// */
//public class YsCameraListAdapter extends BaseQuickAdapter<EZDeviceInfo, BaseViewHolder> {
//
//    public YsCameraListAdapter(List<EZDeviceInfo> data) {
//        super(R.layout.ys_cameralist_small_item, data);
//    }
//
//    @NonNull
//    @Override
//    public List<EZDeviceInfo> getData() {
//        return super.getData();
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, EZDeviceInfo deviceInfo) {
////        helper.setText(R.id.item_family_name, item.getRoomName());
//////        helper.setText(R.id.item_family_info, item.get());
////        helper.addOnClickListener(R.id.item_family_manage);
////        helper.addOnClickListener(R.id.item_family_delete_iv);
////        if (isManageMode) {
////            helper.setVisible(R.id.item_family_delete_iv, true);
////        } else {
////            helper.setVisible(R.id.item_family_delete_iv, false);
////        }
//
//        ImageView iconIv = helper.getView(R.id.item_icon);
//        ImageView playBtn = helper.getView(R.id.item_play_btn);
//        ImageView offlineBtn = helper.getView(R.id.item_offline);
//        TextView cameraNameTv = helper.getView(R.id.camera_name_tv);
//        ImageButton cameraDelBtn = helper.getView(R.id.camera_del_btn);
//        ImageButton alarmListBtn = helper.getView(R.id.tab_alarmlist_btn);
//        ImageButton remoteplaybackBtn = helper.getView(R.id.tab_remoteplayback_btn);
//        ImageButton setDeviceBtn = helper.getView(R.id.tab_setdevice_btn);
//        ImageView offlineBgBtn = helper.getView(R.id.offline_bg);
//        RelativeLayout itemIconArea = helper.getView(R.id.item_icon_area);
//        ImageButton deleteBtn = helper.getView(R.id.camera_del_btn);
//        ImageButton devicePicBtn = helper.getView(R.id.tab_devicepicture_btn);
//        ImageButton deviceVideoBtn = helper.getView(R.id.tab_devicevideo_btn);
//        RelativeLayout deviceDefenceRl = helper.getView(R.id.tab_devicedefence_rl);
//        ImageButton deviceDefenceBtn = helper.getView(R.id.tab_devicedefence_btn);
//        EZCameraInfo cameraInfo = null;
//        if (deviceInfo != null) {
//            cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, 0);
//            helper.addOnClickListener(R.id.item_play_btn);
//            helper.addOnClickListener(R.id.camera_del_btn);
//            helper.addOnClickListener(R.id.tab_alarmlist_btn);
//            helper.addOnClickListener(R.id.tab_remoteplayback_btn);
//            helper.addOnClickListener(R.id.tab_setdevice_btn);
//            helper.addOnClickListener(R.id.tab_devicepicture_btn);
//            helper.addOnClickListener(R.id.tab_devicedefence_btn);
//            if (deviceInfo.getStatus() == 2) {
//                offlineBtn.setVisibility(View.VISIBLE);
//                offlineBgBtn.setVisibility(View.VISIBLE);
//                playBtn.setVisibility(View.GONE);
//                deviceDefenceRl.setVisibility(View.INVISIBLE);
//            } else {
//                offlineBtn.setVisibility(View.GONE);
//                offlineBgBtn.setVisibility(View.GONE);
//                playBtn.setVisibility(View.VISIBLE);
//                deviceDefenceRl.setVisibility(View.VISIBLE);
//            }
//
//            cameraNameTv.setText(deviceInfo.getDeviceName());
//            iconIv.setVisibility(View.VISIBLE);
//            String imageUrl = deviceInfo.getDeviceCover();
//            if(!TextUtils.isEmpty(imageUrl)) {
//                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.device_other).into(iconIv);
//            }
//        }
//
//        if(cameraInfo != null) {
//            // 如果是分享设备，隐藏消息列表按钮和设置按钮
//            if(cameraInfo.getIsShared() != 0 && cameraInfo.getIsShared() != 1) {
//                alarmListBtn.setVisibility(View.GONE);
//                setDeviceBtn.setVisibility(View.GONE);
//            } else {
//                alarmListBtn.setVisibility(View.VISIBLE);
//                setDeviceBtn.setVisibility(View.VISIBLE);
//            }
//        }
//
//    }
//
//
//}
