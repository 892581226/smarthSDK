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
 * date: 2019/4/10 15:09
 * description: 添加设备-设备类型列表适配器
 * update: 2019/4/10
 * version:
 */
public class DeviceAppendAdapter extends BaseQuickAdapter<DeviceListVo.ProductCollectionBean.DevListBean, BaseViewHolder> {
    public DeviceAppendAdapter(List<DeviceListVo.ProductCollectionBean.DevListBean> data) {
        super(R.layout.item_iot_device_append_rv_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListVo.ProductCollectionBean.DevListBean item) {
        helper.setText(R.id.item_iot_device_append_name, item.getDeviceName());

        Glide.with(mContext).load(item.getActualIcon()).error(R.drawable.icon_iot_curtain_control_switch).into((ImageView) helper.getView(R.id.item_iot_device_append_img));
        helper.addOnClickListener(R.id.item_iot_device_append_linear);
    }
}
