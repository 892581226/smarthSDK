package com.example.smarthome.iot.impl.device.infraredscreen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;

public class InFraredScreenListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public InFraredScreenCallBack inFraredScreenCallBack;

    public void setInFraredScreenCallBack(InFraredScreenCallBack inFraredScreenCallBack) {
        this.inFraredScreenCallBack = inFraredScreenCallBack;
    }

    public interface InFraredScreenCallBack{
        void callBack(int position,String location);
    }

    public InFraredScreenListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Context context=helper.itemView.getContext();
        TextView tv_device_name=helper.getView(R.id.tv_device_name);
        tv_device_name.setText(item+helper.getAdapterPosition()+"设备");
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inFraredScreenCallBack.callBack(helper.getAdapterPosition(),item);
            }
        });
    }


}
