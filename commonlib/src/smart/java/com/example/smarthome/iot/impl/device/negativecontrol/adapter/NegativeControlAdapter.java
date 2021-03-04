package com.example.smarthome.iot.impl.device.negativecontrol.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.DeviceBean;
import com.example.smarthome.iot.impl.device.negativecontrol.bean.FkDatsBean;
import com.example.smarthome.iot.impl.device.negativecontrol.bean.NegativeControlBean;

import java.util.Iterator;

public class NegativeControlAdapter extends BaseQuickAdapter<FkDatsBean, BaseViewHolder> {



    public NeGativeCallBack neGativeCallBack;

    public void setNeGativeCallBack(NeGativeCallBack neGativeCallBack) {
        this.neGativeCallBack = neGativeCallBack;
    }

    public interface NeGativeCallBack{
        void callBack(int position,String deviceId,String name);
    }

    public NegativeControlAdapter() {
        super(R.layout.adapter_negative_control_three);
    }

    @Override
    protected void convert(BaseViewHolder helper, FkDatsBean item) {
        Context context=helper.itemView.getContext();
        TextView tv_location=helper.getView(R.id.tv_location);
        TextView tv_name=helper.getView(R.id.tv_name);
        TextView tv_bind=helper.getView(R.id.tv_bind);
        tv_location.setText(item.getLocation());
        tv_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neGativeCallBack.callBack(item.getPosition(),item.getDeviceId(),item.getName());
            }
        });
        tv_name.setText(item.getName());
    }
}
