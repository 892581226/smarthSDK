package com.example.smarthome.iot.impl.device.infraredscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.infraredscreen.bean.InFraredScreenBean;

import java.util.List;

public class InFraredScreenAdapter extends BaseAdapter {


    private Context context;
    private List<InFraredScreenBean.InFraredScreenData> lists;
    private InFraredCallBack inFraredCallBack;

    public void setInFraredCallBack(InFraredCallBack inFraredCallBack) {
        this.inFraredCallBack = inFraredCallBack;
    }

    public interface InFraredCallBack{
        void CallBack(String name,int postion);
    }

    public InFraredScreenAdapter(Context context, List<InFraredScreenBean.InFraredScreenData> lists) {
        this.context=context;
        this.lists=lists;
    }

    public void refreshData(List<InFraredScreenBean.InFraredScreenData> list){
        this.lists=list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (lists==null){
            return 0;
        }
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder;
        if (convertView==null){
            viewHolder=new viewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.adapter_in_frared_screen,parent,false);
            viewHolder.tv_name=convertView.findViewById(R.id.tv_name);
            viewHolder.tv_click_change=convertView.findViewById(R.id.tv_click_change);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(viewHolder) convertView.getTag();
        }

        viewHolder.tv_name.setText(lists.get(position).getName());

        viewHolder.tv_click_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inFraredCallBack.CallBack(lists.get(position).getName(),position);
            }
        });

        return convertView;
    }

    class viewHolder{
        TextView tv_name,tv_click_change;
    }
}
