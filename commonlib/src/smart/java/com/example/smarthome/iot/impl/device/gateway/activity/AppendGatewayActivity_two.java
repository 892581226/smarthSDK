package com.example.smarthome.iot.impl.device.gateway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.entry.GateWayBeanVo;
import com.example.smarthome.iot.impl.device.gateway.adapter.AppendGatewayAdapter;
import com.xhwl.commonlib.base.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class AppendGatewayActivity_two extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView tv_title;
    private ImageView back;
    private ListView list_item;
    private AppendGatewayAdapter appendGatewayAdapter;
    private List<GateWayBeanVo.GateWayBeanVoData> datas;
    private TextView tv_rescan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_append_gateway_two);
        addDestoryActivity(this,"AppendGatewayActivity_two");
        Intent intent=getIntent();
        if (intent!=null){
            datas = (List<GateWayBeanVo.GateWayBeanVoData>) intent.getSerializableExtra("datas");
        }
       /* HashSet<GateWayBeanVo.GateWayBeanVoData> set = new HashSet<>();
        for (Iterator iter=datass.iterator();iter.hasNext();){
            GateWayBeanVo.GateWayBeanVoData next = (GateWayBeanVo.GateWayBeanVoData)iter.next();
            if (set.add(next)){
                datas.add(next);
            }
        }*/
        Log.e("TAG", "onCreate: "+datas.size()+"----"+datas) ;
        initView();
        initDate();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        list_item = findViewById(R.id.list_item);
        tv_rescan = findViewById(R.id.tv_rescan);
        tv_rescan.setOnClickListener(this);
    }
    private void initDate() {
        tv_title.setText("添加设备");
        appendGatewayAdapter = new AppendGatewayAdapter(AppendGatewayActivity_two.this,datas);
        list_item.setAdapter(appendGatewayAdapter);
        appendGatewayAdapter.setCallBack(new AppendGatewayAdapter.CallBack() {
            @Override
            public void back(GateWayBeanVo.GateWayBeanVoData datas,int postion) {
                Intent intent=new Intent(AppendGatewayActivity_two.this,GatewayNetSettingActivity_gateway.class);
                intent.putExtra("datas",(Serializable) datas);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.back){
            finish();
        }else if (v.getId()==R.id.tv_rescan){
            //重新扫描
            Intent intent=new Intent(AppendGatewayActivity_two.this,AppendGatewayActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
