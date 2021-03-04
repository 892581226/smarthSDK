package com.example.smarthome.iot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.impl.DeviceAppendActivity;

import java.util.List;

/**
 * author: glq
 * date: 2019/4/10 13:38
 * description: 全部设备类别列表
 * update: 2019/4/10
 * version:
 */
public class DeviceCategoryListAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceListVo.ProductCollectionBean> beanList;
    public static int mPosition;

    public DeviceCategoryListAdapter(Context context, List<DeviceListVo.ProductCollectionBean> stringList){
        this.context =context;
        this.beanList = stringList;
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_iot_device_list_view_layout, null);
        TextView tv = (TextView) convertView.findViewById(R.id.item_iot_device_text);
        View iv = convertView.findViewById(R.id.item_iot_device_icon);
        mPosition = position;
        tv.setText(beanList.get(position).getDeviceKind());
        if (position == DeviceAppendActivity.mPosition) {
            convertView.setBackgroundResource(R.color.white);
            tv.setTextColor(Color.parseColor("#598DF3"));
            iv.setVisibility(View.VISIBLE);
        } else {
            convertView.setBackgroundColor(Color.parseColor("#F8F8F8"));
            iv.setVisibility(View.GONE);
        }
        return convertView;
    }
}
