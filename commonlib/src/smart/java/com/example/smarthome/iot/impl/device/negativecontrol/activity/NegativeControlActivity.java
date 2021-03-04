package com.example.smarthome.iot.impl.device.negativecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.xhwl.commonlib.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NegativeControlActivity extends BaseActivity implements View.OnClickListener {


    private LinearLayout top_back;
    private TextView top_title;
    private TextView top_btn;
    private ImageView image_switch_1;
    private ImageView image_switch_2;
    private ImageView image_switch_3;
    private ImageView image_switch_4;
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negative_control);
        Intent intent=getIntent();
        if (intent!=null){
            gateDevices = intent.getParcelableExtra("gateDevices");
            Log.e("辅控开关_控制",gateDevices.toString()+" "+gateDevices.getId());
        }
        initView();
        initDate();
    }
    private void initView(){
        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        image_switch_1 = findViewById(R.id.image_switch_1);
        image_switch_2 = findViewById(R.id.image_switch_2);
        image_switch_3 = findViewById(R.id.image_switch_3);
        image_switch_4 = findViewById(R.id.image_switch_4);
        top_back.setOnClickListener(this);
        image_switch_1.setOnClickListener(this);
        image_switch_2.setOnClickListener(this);
        image_switch_3.setOnClickListener(this);
        image_switch_4.setOnClickListener(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            top_title.setText(messageEvent.getTitle());
        }
    }

    private void initDate(){
        top_btn.setText("管理");
        top_btn.setVisibility(View.VISIBLE);
        top_btn.setOnClickListener(this);
        top_title.setText(gateDevices.getDeviceName());

        top_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NegativeControlActivity.this, DeviceInfoActivity.class);
                intent.putExtra("deviceInfoBean",gateDevices);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent intent=new Intent();
        intent.setClass(NegativeControlActivity.this,NegativeControlActivity_two.class);
        intent.putExtra("deviceId",gateDevices.getDeviceId());
        if (id==R.id.top_back){
            finish();
            return;
        }else if(id==R.id.image_switch_1){
            intent.putExtra("switch_number",1);
        }else if(id==R.id.image_switch_2){
            intent.putExtra("switch_number",2);
        }else if(id==R.id.image_switch_3){
            intent.putExtra("switch_number",3);
        }else if(id==R.id.image_switch_4){
            intent.putExtra("switch_number",4);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
