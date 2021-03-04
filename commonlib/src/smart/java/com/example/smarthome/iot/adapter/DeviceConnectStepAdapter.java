package com.example.smarthome.iot.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.DeviceConnectStepItemVo;


import java.util.List;

/**
 * author: glq
 * date: 2019/5/9 13:46
 * description: 设备接入流程列表适配器
 * update: 2019/5/9
 * version:
 */
public class DeviceConnectStepAdapter extends BaseQuickAdapter<DeviceConnectStepItemVo, BaseViewHolder> {
    public DeviceConnectStepAdapter(List<DeviceConnectStepItemVo> data) {
        super(R.layout.item_iot_device_connect_step_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceConnectStepItemVo item) {
        Glide.with(mContext).load(item.getIcon()).error(R.drawable.icon_iot_curtain_control_switch).into((ImageView) helper.getView(R.id.item_iot_device_connect_step_img));
        helper.setText(R.id.item_iot_device_connect_step_intro, item.getTips());
        helper.setText(R.id.item_iot_device_connect_step_number, item.getStep());
//        switch (item.getDeviceStepIntro()) {
//            case DeviceType.HILINK_CURTAIN:
//                // 窗帘
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_curtain_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_curtain_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                }
//                break;
//            case DeviceType.HILINK_DOOR_MAGNETIC:
//                // 门磁
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_door_magentic_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_door_magentic_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                }
//                break;
//            case DeviceType.HILINK_GAS_ALARM:
//                // 燃气报警
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_curtain_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_curtain_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                }
//                break;
//            case DeviceType.HILINK_HUMAN_INFRARED:
//                // 人体红外
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_humen_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_humen_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                }
//                break;
//            case DeviceType.HILINK_SCENE_SWITCH_6:
//                // 六路场景开关
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_sence_switch_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_sence_switch_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                }
//                break;
//            case DeviceType.HILINK_SMOKE_ALARM:
//                // 烟雾报警
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_sence_switch_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_sence_switch_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                }
//                break;
//            case DeviceType.HILINK_SWITCH_1:
//                // 一位开关
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_one_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_one_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_one_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_2);
//                }
//                break;
//            case DeviceType.HILINK_SWITCH_2:
//                // 二位开关
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_two_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_two_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_two_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_2);
//                }
//                break;
//            case DeviceType.HILINK_SWITCH_3:
//                // 三位开关
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_three_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_three_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_three_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_2);
//                }
//                break;
//            case DeviceType.HILINK_SWITCH_4:
//                // 四位开关
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_four_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_four_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_switch_four_2);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_switch_2);
//                }
//                break;
//            case DeviceType.HILINK_TEMP_HUN:
//                // 温湿度传感
//                if (item.getDeviceStepNumber() == 1) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_humiture_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_1);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_1);
//                } else if (item.getDeviceStepNumber() == 2) {
//                    helper.setImageResource(R.id.item_iot_device_connect_step_img, R.drawable.icon_iot_step_humiture_1);
//                    helper.setText(R.id.item_iot_device_connect_step_number, R.string.step_2);
//                    helper.setText(R.id.item_iot_device_connect_step_intro, R.string.step_sensor_2);
//                }
//                break;
//            default:
//
//                break;
//        }
    }
}
