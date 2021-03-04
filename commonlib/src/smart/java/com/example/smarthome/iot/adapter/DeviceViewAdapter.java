package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.entry.SmartInfoVo;

import java.util.List;

/**
 * author:
 * date:
 * description: 设备展示适配器
 * update:
 * version:
 */
public class DeviceViewAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.DeviceInfoBean, BaseViewHolder> {

    public DeviceViewAdapter(List<SmartInfoVo.FamilysBean.DeviceInfoBean> data) {
        super(R.layout.item_iot_device_layout,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.DeviceInfoBean item) {
        helper.addOnClickListener(R.id.item_iot_device_linear);
        helper.addOnClickListener(R.id.item_iot_device_switch);

        helper.setText(R.id.item_iot_device_name,item.getDeviceName());
        helper.setText(R.id.item_iot_device_address,item.getRoomName());
        ImageView imageView = helper.getView(R.id.item_iot_device_switch);
        ImageView imageView1 = helper.getView(R.id.item_iot_device_online_type_img);

        if (item.isOnline()) { // 是否在线
            imageView1.setImageResource(R.drawable.icon_iot_device_online);
            helper.setText(R.id.item_iot_device_online_type_text,"在线");
        } else {
            imageView1.setImageResource(R.drawable.icon_iot_device_offline);
            helper.setText(R.id.item_iot_device_online_type_text,"离线");
        }

        Glide.with(mContext).load(item.getDeviceIcon()).error(R.drawable.icon_iot_curtain_control_switch).into((ImageView) helper.getView(R.id.item_iot_device_img));
        if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)){
            if(item.getField().getSwitch1().equalsIgnoreCase("1")){
                imageView.setImageResource(R.drawable.icon_iot_scene_open);
            } else {
                imageView.setImageResource(R.drawable.icon_iot_scene_close);
            }
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }


    }
}
