package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.DeviceManageItemVo;

import java.util.List;

/**
 * author: glq
 * date: 2019/4/9 13:40
 * description: 设备管理适配器
 * update: 2019/4/9
 * version:
 */
public class DeviceManageAdapter extends BaseQuickAdapter<DeviceManageItemVo, BaseViewHolder> {

    public DeviceManageAdapter(List<DeviceManageItemVo> data) {
        super(R.layout.item_iot_device_layout,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceManageItemVo item) {
        helper.addOnClickListener(R.id.item_iot_device_linear);
        helper.addOnClickListener(R.id.item_iot_device_switch);

        helper.setText(R.id.item_iot_device_name,item.getDeviceName());
        helper.setText(R.id.item_iot_device_address,item.getDeviceAddress());
        ImageView imageView = helper.getView(R.id.item_iot_device_switch);
        ImageView imageView1 = helper.getView(R.id.item_iot_device_online_type_img);
        if (item.isOptional()) {
            imageView.setVisibility(View.VISIBLE);
            if (item.isCheck()) {
                imageView.setImageResource(R.drawable.icon_iot_no_choose);
            } else {
                imageView.setImageResource(R.drawable.icon_iot_choose);
            }
            if (item.isDeviceOnlineType()) { // 是否在线
                imageView1.setImageResource(R.drawable.icon_iot_device_online);
                helper.setText(R.id.item_iot_device_online_type_text,"在线");
            } else {
                imageView1.setImageResource(R.drawable.icon_iot_device_offline);
                helper.setText(R.id.item_iot_device_online_type_text,"离线");
            }
        } else {
            if (item.isHaveSwitch()) { // 是否有开关
                imageView.setVisibility(View.VISIBLE);
                if (item.isDeviceSwitchState()) {
                    imageView.setImageResource(R.drawable.icon_iot_scene_open);
                } else {
                    imageView.setImageResource(R.drawable.icon_iot_scene_close);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
            if (item.isDeviceOnlineType()) { // 是否在线
                imageView1.setImageResource(R.drawable.icon_iot_device_online);
                helper.setText(R.id.item_iot_device_online_type_text,"在线");
            } else {
                imageView1.setImageResource(R.drawable.icon_iot_device_offline);
                helper.setText(R.id.item_iot_device_online_type_text,"离线");
            }
        }




    }
}
