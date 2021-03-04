package com.example.smarthome.iot.adapter;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceType;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.xhwl.commonlib.uiutils.StringUtils;

import java.util.List;

/**
 * author:
 * date:
 * description: 场景设备展示适配器
 * update:
 * version:
 */
public class SceneDeviceAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.DeviceInfoBean, BaseViewHolder> {

    public SceneDeviceAdapter(List<SmartInfoVo.FamilysBean.DeviceInfoBean> data) {
        super(R.layout.scene_item_line_dev,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.DeviceInfoBean item) {

        ImageView imageView = helper.getView(R.id.scene_item_device_icon);
        TextView nameTv = helper.getView(R.id.scene_item_device_name);
        TextView statuTv = helper.getView(R.id.scene_item_device_status);
        
        Glide.with(mContext).load(item.getDeviceIcon()).error(R.drawable.icon_iot_curtain_control_switch).into(imageView);

        nameTv.setText(item.getDeviceName());
        if(item.getDelayTime()>0 && item.getDelayTime()<10000){
            helper.setText(R.id.scene_item_time,"延时0"+item.getDelayTime()/1000+"秒");
        } else if(item.getDelayTime()>=10000){
            helper.setText(R.id.scene_item_time,"延时"+item.getDelayTime()/1000+"秒");
        } else {
            helper.setText(R.id.scene_item_time,"延时00秒");
        }

        String switchStatus = "<Data>";
        if (item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_CURTAIN)){
            if (item.getField()!=null){
                if (item.getField().getState()!=null){
                    if (item.getField().getState().equalsIgnoreCase("1")){
                        switchStatus="窗帘开";
                    }else if (item.getField().getState().equalsIgnoreCase("0")){
                        switchStatus="窗帘关";
                    }
                }

            }
        }else if (item.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_SMAET_SOCKET)
                ||item.getDeviceType().equalsIgnoreCase(DeviceType.HILINK_86_RECEPTACLE) ){
            if (item.getField()!=null){
                if (item.getField().getState()!=null){
                    switchStatus = (item.getField().getState().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
                }
                if (!StringUtils.isEmpty(item.getField().getSwitch1())) {
                    switchStatus = (item.getField().getSwitch1().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
                }
            }
        }else if (item.getDeviceType().equalsIgnoreCase(DeviceType.HONGYAN_DOUBLE_CURTAIN)){
            if (item.getField()!=null){
                if (!StringUtils.isEmpty(item.getField().getOperate1())) {
                    switchStatus = "轨道一" + (item.getField().getOperate1().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
                }
                if (!StringUtils.isEmpty(item.getField().getOperate2())) {
                    switchStatus += ",轨道二" + (item.getField().getOperate2().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
                }
            }
        }else {
            if (!StringUtils.isEmpty(item.getField().getSwitch1())) {
                switchStatus = "第一位" + (item.getField().getSwitch1().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
            }
            if (!StringUtils.isEmpty(item.getField().getSwitch2())) {
                switchStatus += ",第二位" + (item.getField().getSwitch2().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
            }
            if (!StringUtils.isEmpty(item.getField().getSwitch3())) {
                switchStatus += ",第三位" + (item.getField().getSwitch3().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
            }
            if (!StringUtils.isEmpty(item.getField().getSwitch4())) {
                switchStatus += ",第四位" + (item.getField().getSwitch4().equals("1") ? "<font color=\"#598DF3\">开</font>" : "<font color=\"#FA6757\">关</font>");
            }
        }
        switchStatus = switchStatus+"</Data>";
        statuTv.setText(Html.fromHtml(switchStatus));

        if(helper.getAdapterPosition()==0){
            helper.getView(R.id.item_line_up).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.item_line_up).setVisibility(View.VISIBLE);
        }

        if(helper.getAdapterPosition()==getData().size()-1){
            helper.getView(R.id.item_line_down).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_line_down).setVisibility(View.VISIBLE);
        }


        helper.addOnClickListener(R.id.scene_item_time);
        helper.addOnClickListener(R.id.scene_item_device_delete_layout);
        helper.addOnClickListener(R.id.scene_item_ll);
    }
}
