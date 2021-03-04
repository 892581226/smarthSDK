package com.example.smarthome.iot.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.SwitchStatusBeanVo;

import java.util.List;

/**
 * author:
 * date:
 * description: 场景开关适配器
 * update:
 * version:
 */
public class SceneSwitchStatusAdapter2 extends BaseQuickAdapter<SwitchStatusBeanVo, BaseViewHolder> {

    public SceneSwitchStatusAdapter2(List<SwitchStatusBeanVo> data) {
        super(R.layout.scene_item_switch_status,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SwitchStatusBeanVo item) {
        helper.addOnClickListener(R.id.scene_switch_status_cl);

        helper.setText(R.id.scene_switch_status_tv,item.getSwitchName());
        ImageView imageView = helper.getView(R.id.scene_switch_status_iv);
//        if (helper.getAdapterPosition()==0){
            if(item.isSwitchStatus()){
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_iot_choose));
            } else {
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_iot_no_choose));
            }
//        }

    }
}
