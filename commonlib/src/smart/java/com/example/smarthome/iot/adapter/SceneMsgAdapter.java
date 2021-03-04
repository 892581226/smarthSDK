package com.example.smarthome.iot.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.entry.SensorInfoVo;
import com.google.gson.Gson;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.List;

/**
 * author:
 * date:
 * description: 传感设备Msg
 * update:
 * version:
 */
public class SceneMsgAdapter extends BaseQuickAdapter<SensorInfoVo.SensorMessages, BaseViewHolder> {
    private String devType;

    public SceneMsgAdapter(List<SensorInfoVo.SensorMessages> data) {
        super(R.layout.item_line_text,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SensorInfoVo.SensorMessages item) {
        View divideLine = helper.getView(R.id.item_line_divide);
        if(!StringUtils.isEmpty(item.getMsg())){
            if(devType.equals(DeviceType.HILINK_SCENE_SWITCH_6)){
                helper.setText(R.id.item_line_time,item.getTime().substring(0,19));
                helper.setText(R.id.item_line_content,item.getMsg());
            }else {
                helper.setText(R.id.item_line_time,item.getTime().substring(0,4)+"."+
                        item.getTime().substring(4,6)+"."+item.getTime().substring(6,8)
                        +"  "+item.getTime().substring(8,10)+":"+item.getTime().substring(10,12));
                SensorInfoVo.SensorMsg sensorMsg = new Gson().fromJson(item.getMsg(), SensorInfoVo.SensorMsg.class);
                if(sensorMsg!=null){
                    if(devType.equals(DeviceType.HILINK_HUMAN_INFRARED)){
                        helper.setText(R.id.item_line_content,sensorMsg.getAlarm().equals("1")?"检测到有人移动":"复位");
                    } else if(devType.equals(DeviceType.HILINK_DOOR_MAGNETIC)){
                        helper.setText(R.id.item_line_content,sensorMsg.getAlarm().equals("1")?"门已打开":"门已关闭");
                    } else if(devType.equals(DeviceType.HILINK_WATER_ALARM)){
                        helper.setText(R.id.item_line_content,sensorMsg.getAlarm().equals("1")?"水侵报警":"检测正常");
                    }
                } else {
                    helper.setText(R.id.item_line_content,"");
                }
            }


        }

        if(helper.getAdapterPosition()==0){
            helper.getView(R.id.item_line_up).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.item_line_up).setVisibility(View.VISIBLE);
        }
        if(helper.getAdapterPosition()==getData().size()-1){
            helper.getView(R.id.item_line_down).setVisibility(View.GONE);
            divideLine.setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_line_down).setVisibility(View.VISIBLE);
            divideLine.setVisibility(View.VISIBLE);
        }
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }
}
