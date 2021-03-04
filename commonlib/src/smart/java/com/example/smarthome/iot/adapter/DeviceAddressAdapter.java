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
public class DeviceAddressAdapter extends BaseQuickAdapter<SmartInfoVo.FamilysBean.RoomsBean, BaseViewHolder> {
    private List<SmartInfoVo.FamilysBean.RoomsBean> data;
    public DeviceAddressAdapter(List<SmartInfoVo.FamilysBean.RoomsBean> data) {
        super(R.layout.item_iot_flex_box_layout,data);
        this.data = data;
    }


    @Override
    protected void convert(BaseViewHolder helper, SmartInfoVo.FamilysBean.RoomsBean item) {
        helper.addOnClickListener(R.id.item_ll);
        TextView textView = helper.getView(R.id.item_tv);
        textView.setText(item.getRoomName());
        if(item.isChecked()){
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_blue_sold_17t));
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_blue_corner_white_17t));
            textView.setTextColor(mContext.getResources().getColor(R.color.text_gray_a5_color));
        }
    }

    public SmartInfoVo.FamilysBean.RoomsBean getClickedItem(){
        for(int i =0;i<data.size();i++){
            if(data.get(i).isChecked()){
                return data.get(i);
            }
        }
        return null;
    }

}
