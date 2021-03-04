package com.example.smarthome.iot.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.IconUpdateItemVo;

import java.util.List;

/**
 * author: glq
 * date: 2019/4/12 9:40
 * description:
 * update: 2019/4/12
 * version:
 */
public class IconUpdateAdapter extends BaseQuickAdapter<IconUpdateItemVo, BaseViewHolder> {

    public IconUpdateAdapter(List<IconUpdateItemVo> data) {
        super(R.layout.item_iot_icon_update_layout,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, IconUpdateItemVo item) {
        helper.setImageResource(R.id.item_iot_update_icon,item.getIconId());
        helper.addOnClickListener(R.id.item_iot_update_icon);
    }
}
