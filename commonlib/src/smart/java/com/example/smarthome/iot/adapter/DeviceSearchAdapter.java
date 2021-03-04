package com.example.smarthome.iot.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.DeviceListVo;

import java.util.List;

/**
 * author: glq
 * date: 2019/4/12 14:56
 * description:
 * update: 2019/4/12
 * version:
 */
public class DeviceSearchAdapter extends BaseQuickAdapter<DeviceListVo.ProductCollectionBean.DevListBean, BaseViewHolder> {
    public DeviceSearchAdapter(List<DeviceListVo.ProductCollectionBean.DevListBean> data) {
        super(R.layout.item_iot_device_search_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListVo.ProductCollectionBean.DevListBean item) {
        ImageView imageView = helper.getView(R.id.item_iot_device_search_img);
        helper.setText(R.id.item_iot_device_search_name, item.getDeviceName());
        Glide.with(mContext).load(item.getActualIcon()).error(R.drawable.icon_iot_curtain_control_switch).into(imageView);
        helper.addOnClickListener(R.id.item_iot_device_search_item);
    }
}
