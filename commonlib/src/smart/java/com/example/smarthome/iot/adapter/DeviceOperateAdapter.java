package com.example.smarthome.iot.adapter;

import android.bluetooth.BluetoothClass;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author:
 * date:
 * description: 全部设备 可操作适配器
 * update:
 * version:
 */
public class DeviceOperateAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.DeviceInfoBean, BaseViewHolder> {
    List<SmartInfoVo.FamilysBean.DeviceInfoBean> data;
    List<SmartInfoVo.FamilysBean.DeviceInfoBean> datas;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setArray(List<SmartInfoVo.FamilysBean.DeviceInfoBean> datas) {
        this.datas = datas;
    }

    public DeviceOperateAdapter(List<SmartInfoVo.FamilysBean.DeviceInfoBean> data) {
        super(R.layout.item_iot_device_layout, data);
        this.data = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.DeviceInfoBean item) {
        helper.setText(R.id.item_iot_device_name, item.getDeviceName());
        helper.setText(R.id.item_iot_device_address, item.getLocation());
        ImageView imageView = helper.getView(R.id.item_iot_device_switch);
        ImageView imageView1 = helper.getView(R.id.item_iot_device_online_type_img);
        imageView.setVisibility(View.VISIBLE);
        Log.e("适配器数据",item.toString()+" "+from);
        Glide.with(mContext)
                .load(item.getDeviceIcon())
                .error(R.drawable.icon_iot_curtain_control_switch)
                .into((ImageView) helper
                        .getView(R.id.item_iot_device_img));
        if (!StringUtils.isEmpty(from) && !from.equalsIgnoreCase("SceneAppendActivity") && !from.equalsIgnoreCase("SceneUpdateActivity")) {
            if (datas.size()>0){
                for (SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean: datas){
                    if (deviceInfoBean.getDeviceId().equals(item.getDeviceId())){
                        helper.getView(R.id.item_iot_device_linear).setClickable(false);
                        imageView.setImageResource(R.drawable.icon_iot_no_choose);
                        return;
                    }else {
                        if (StringUtils.isEmpty(item.getRoomName())) {
                            if(item.isCheck()){
                                imageView.setImageResource(R.drawable.icon_iot_choose);
                            } else {
                                imageView.setImageResource(R.drawable.icon_iot_unselected);
                            }
                            helper.addOnClickListener(R.id.item_iot_device_linear);
                        } else {
                            helper.getView(R.id.item_iot_device_linear).setClickable(false);
                            // 不可选中状态
                            imageView.setImageResource(R.drawable.icon_iot_no_choose);
                        }
                    }
                }
            }else {
                if (StringUtils.isEmpty(item.getRoomName())) {
                    if(item.isCheck()){
                        imageView.setImageResource(R.drawable.icon_iot_choose);
                    } else {
                        imageView.setImageResource(R.drawable.icon_iot_unselected);
                    }
                    helper.addOnClickListener(R.id.item_iot_device_linear);
                } else {
                    helper.getView(R.id.item_iot_device_linear).setClickable(false);
                    // 不可选中状态
                    imageView.setImageResource(R.drawable.icon_iot_no_choose);
                }
            }
        } else if (from.equalsIgnoreCase("SceneUpdateActivity")||from.equalsIgnoreCase("SceneAppendActivity")){
            if (DeviceType.HILINK_SWITCH_1.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_SWITCH_2.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_SWITCH_3.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_SWITCH_4.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_CURTAIN.equals(item.getDeviceType()) ||
                    DeviceType.HONGYAN_SWITCH_1.equals(item.getDeviceType()) ||
                    DeviceType.HONGYAN_SWITCH_2.equals(item.getDeviceType()) ||
                    DeviceType.HONGYAN_SWITCH_3.equals(item.getDeviceType()) ||
                    DeviceType.HONGYAN_SWITCH_4.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_PLC_SWITCH_1.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_PLC_SWITCH_2.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_PLC_SWITCH_3.equals(item.getDeviceType()) ||
                    DeviceType.HILINK_PLC_SWITCH_4.equals(item.getDeviceType()) ||
                    DeviceType.CURTAIN_1.equals(item.getDeviceType()) ||
                    DeviceType.CURTAIN_2.equals(item.getDeviceType()) ||
                    DeviceType.CURTAIN_3.equals(item.getDeviceType()) ||
                    DeviceType.HONGYAN_SMAET_SOCKET.equals(item.getDeviceType())||
                    DeviceType.HILINK_86_RECEPTACLE.equals(item.getDeviceType())||
                    DeviceType.HONGYAN_DOUBLE_CURTAIN.equals(item.getDeviceType())||
                    item.getDeviceType().equalsIgnoreCase(DeviceType.KONGKE_SWITCH_1)||
                    item.getDeviceType().equalsIgnoreCase(DeviceType.KONGKE_SWITCH_2)||
                    item.getDeviceType().equalsIgnoreCase(DeviceType.KONGKE_SWITCH_3)) {
                if (datas.size()>0){
                    for (SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean: datas){
                        if (deviceInfoBean.getDeviceId().equals(item.getDeviceId())){
                            helper.getView(R.id.item_iot_device_linear).setClickable(false);
                            imageView.setImageResource(R.drawable.icon_iot_no_choose);
                            return;
                        }else {
                            if (item.isCheck()) {
                                imageView.setImageResource(R.drawable.icon_iot_choose);
                            } else {
                                imageView.setImageResource(R.drawable.icon_iot_unselected);
                            }
                            helper.addOnClickListener(R.id.item_iot_device_linear);
                        }
                    }


                }else {
                    if (item.isCheck()) {
                        imageView.setImageResource(R.drawable.icon_iot_choose);
                    } else {
                        imageView.setImageResource(R.drawable.icon_iot_unselected);
                    }
                    helper.addOnClickListener(R.id.item_iot_device_linear);
                }

            } else {
                helper.getView(R.id.item_iot_device_linear).setClickable(false);
                imageView.setImageResource(R.drawable.icon_iot_no_choose);
            }

        }

        if (item.isOnline()) { // 是否在线
            imageView1.setImageResource(R.drawable.icon_iot_device_online);
            helper.setText(R.id.item_iot_device_online_type_text, "在线");
        } else {
            imageView1.setImageResource(R.drawable.icon_iot_device_offline);
            helper.setText(R.id.item_iot_device_online_type_text, "离线");
        }

    }

    public List<SmartInfoVo.FamilysBean.DeviceInfoBean> getCheckedList() {
        List<SmartInfoVo.FamilysBean.DeviceInfoBean> list = new ArrayList<>();
        for(int i =0;i<data.size();i++){
            if(data.get(i).isCheck()){
                list.add(data.get(i));
            }
        }
        return list;
    }
}
