package com.example.smarthome.iot.impl.device.gateway.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.entry.GateWayBeanVo;

import java.util.List;

public class AppendGatewayAdapter extends BaseAdapter {


    private Context context;
    private List<GateWayBeanVo.GateWayBeanVoData> lists;
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack{
        void back(GateWayBeanVo.GateWayBeanVoData datas,int postion);
    }


    public AppendGatewayAdapter(Context context, List<GateWayBeanVo.GateWayBeanVoData> lists) {
        this.context=context;
        this.lists=lists;
    }

    public void refreshDatas(List<GateWayBeanVo.GateWayBeanVoData> lists){
        this.lists=lists;
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.adapter_append_gateway,parent,false);
            viewHolder.tv_name=convertView.findViewById(R.id.tv_name);
            viewHolder.tv_to_connect=convertView.findViewById(R.id.tv_to_connect);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_name.setText(lists.get(position).getDeviceName());
     /*   if (lists.get(position).isRegiste()==true){
            viewHolder.tv_to_connect.setText("已注册");
            int color = context.getResources().getColor(R.color.gray);
            viewHolder.tv_to_connect.setTextColor(color);
            viewHolder.tv_to_connect.setClickable(false);
        }else {*/
            viewHolder.tv_to_connect.setText("连接");
            viewHolder.tv_to_connect.setTextColor(0xff0064EB);
            viewHolder.tv_to_connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.back(lists.get(position),position);
                }
            });

        //}

        return convertView;
    }

    class ViewHolder{
        TextView tv_name,tv_to_connect;
    }
}
