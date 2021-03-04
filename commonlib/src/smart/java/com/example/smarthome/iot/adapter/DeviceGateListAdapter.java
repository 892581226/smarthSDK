package com.example.smarthome.iot.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.entry.SmartInfoVo;

import java.util.List;

/**
 * 设备包括网关
 */
public class DeviceGateListAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.DeviceInfoBean, BaseViewHolder> {

    public DeviceGateListAdapter(List<SmartInfoVo.FamilysBean.DeviceInfoBean> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<SmartInfoVo.FamilysBean.DeviceInfoBean>() {
            @Override
            protected int getItemType(SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean) {
                return deviceInfoBean.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_GATEWAY)?1:0;
            }
        });

        getMultiTypeDelegate()
                .registerItemType(1,R.layout.item_iot_gateway_layout)
                .registerItemType(0,R.layout.item_iot_device_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.DeviceInfoBean item) {
//        if(item !=null){
//            helper.addOnClickListener(R.id.item_iot_device_linear);
//            helper.addOnClickListener(R.id.item_iot_device_switch);
//
//            TextView deviceName = helper.getView(R.id.item_iot_device_name);
//            deviceName.setText(item.getDeviceName());
////            helper.setText(R.id.item_iot_device_name, item.getDeviceName());
//            helper.setText(R.id.item_iot_device_address, item.getLocation());
//            ImageView imageView = helper.getView(R.id.item_iot_device_switch);
//            ImageView imageView1 = helper.getView(R.id.item_iot_device_online_type_img);
//            TextView devOnline = helper.getView(R.id.item_iot_device_online_type_text);
//            if (item.isOnline()) { // 是否在线
//                imageView1.setImageResource(R.drawable.icon_iot_device_online);
//                devOnline.setText("在线");
//                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_3E3E3E));
////                helper.setText(R.id.item_iot_device_online_type_text, "在线");
//                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_333333));
//            } else {
//                imageView1.setImageResource(R.drawable.icon_iot_device_offline);
//                devOnline.setText("离线");
//                devOnline.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
////                helper.setText(R.id.item_iot_device_online_type_text, "离线");
//                deviceName.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
//            }
//
////            int icon = MyAPP.getIns().getResource(item.getDeviceIcon(),"drawable",R.drawable.icon_iot_curtain_control_switch);
////            helper.setImageResource(R.id.item_iot_device_img, icon);
//
//            Glide.with(mContext).load(item.getDeviceIcon()).error(R.drawable.icon_iot_curtain_control_switch).into((ImageView) helper.getView(R.id.item_iot_device_img));
//            if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)){
//                imageView.setImageResource(R.drawable.icon_iot_scene_open);
//                imageView.setVisibility(View.VISIBLE);
//            } else {
//                imageView.setVisibility(View.GONE);
//            }
//        }
        switch (helper.getItemViewType()){
            case 0:
                    if(item !=null){
                        helper.addOnClickListener(R.id.item_iot_device_linear);
                        helper.addOnClickListener(R.id.item_iot_device_switch);

                        TextView deviceName = helper.getView(R.id.item_iot_device_name);
                        deviceName.setText(item.getDeviceName());
            //            helper.setText(R.id.item_iot_device_name, item.getDeviceName());
                        helper.setText(R.id.item_iot_device_address, item.getLocation());
                        ImageView imageView = helper.getView(R.id.item_iot_device_switch);
                        ImageView imageView1 = helper.getView(R.id.item_iot_device_online_type_img);
                        TextView devOnline = helper.getView(R.id.item_iot_device_online_type_text);
                        if (item.isOnline()) { // 是否在线
                            imageView1.setImageResource(R.drawable.icon_iot_device_online);
                            devOnline.setText("在线");
                            devOnline.setTextColor(mContext.getResources().getColor(R.color.color_3E3E3E));
            //                helper.setText(R.id.item_iot_device_online_type_text, "在线");
                            deviceName.setTextColor(mContext.getResources().getColor(R.color.color_333333));
                        } else {
                            imageView1.setImageResource(R.drawable.icon_iot_device_offline);
                            devOnline.setText("离线");
                            devOnline.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
            //                helper.setText(R.id.item_iot_device_online_type_text, "离线");
                            deviceName.setTextColor(mContext.getResources().getColor(R.color.color_9E9E9E));
                        }

            //            int icon = MyAPP.getIns().getResource(item.getDeviceIcon(),"drawable",R.drawable.icon_iot_curtain_control_switch);
            //            helper.setImageResource(R.id.item_iot_device_img, icon);

                        Glide.with(mContext).load(item.getDeviceIcon()).error(R.drawable.icon_iot_curtain_control_switch).into((ImageView) helper.getView(R.id.item_iot_device_img));
                        if(item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_SWITCH_1)){
                            imageView.setImageResource(R.drawable.icon_iot_scene_open);
                            imageView.setVisibility(View.VISIBLE);
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                    }
                break;
            case 1:
                //网关
                ImageView imageView = helper.getView(R.id.item_iot_gw_img);
                imageView.setImageResource(R.drawable.icon_iot_double_control_switch);
                helper.getView(R.id.item_iot_gw_switch).setVisibility(View.GONE);
//                Glide.with(mContext).load(item.getDeviceIcon()).into((ImageView) helper.getView(R.id.item_iot_gw_img));
                break;
        }
    }
}
