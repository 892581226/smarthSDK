package com.example.smarthome.iot.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.SmartInfoVo;

import java.util.List;

/**
 * author: glq
 * date: 2019/5/17 17:14
 * description:
 * update: 2019/5/17
 * version:
 */
public class AttchSceneAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.ScenesBean, BaseViewHolder> {
    private List<SmartInfoVo.FamilysBean.ScenesBean> data;
    public AttchSceneAdapter(List<SmartInfoVo.FamilysBean.ScenesBean> data) {
        super(R.layout.item_iot_flex_box_layout,data);
        this.data = data;
    }


    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.ScenesBean item) {
        helper.addOnClickListener(R.id.item_ll);
        TextView textView = helper.getView(R.id.item_tv);
        textView.setText(item.getSceneName());
        if(item.isChecked()){
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_blue_sold_17t));
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_blue_corner_white_17t));
            textView.setTextColor(mContext.getResources().getColor(R.color.base_blue));
        }
    }

    public SmartInfoVo.FamilysBean.ScenesBean getClickedItem(){
        for(int i =0;i<data.size();i++){
            if(data.get(i).isChecked()){
                return data.get(i);
            }
        }
        return null;
    }

}
