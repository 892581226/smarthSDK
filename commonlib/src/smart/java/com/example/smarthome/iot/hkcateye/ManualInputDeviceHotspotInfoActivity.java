package com.example.smarthome.iot.hkcateye;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.hkcateye.catutil.IntentConstants;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;


public class ManualInputDeviceHotspotInfoActivity extends RootActivity implements View.OnClickListener {

    private String mEzvizDeviceHotspotPrefix = "EZVIZ_";
    private LinearLayout mTopBack;
    private TextView mTopTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("AP配网");

        setContentView(R.layout.activity_manual_input_device_hotspot_info);
        addDestoryActivity(this,"ManualInputDeviceHotspotInfoActivity");
        EditText app_common_device_hotspot_name = findViewById(R.id.app_common_device_hotspot_name);
        EditText app_common_device_hotspot_pwd = findViewById(R.id.app_common_device_hotspot_pwd);
        String deviceHotspotName = app_common_device_hotspot_name.getText().toString();
        String deviceHotspotPwd = app_common_device_hotspot_pwd.getText().toString();
        if (TextUtils.isEmpty(deviceHotspotName)){
            deviceHotspotName = mEzvizDeviceHotspotPrefix + getIntent().getStringExtra(IntentConstants.DEVICE_SERIAL);
        }
        Intent jumpIntent =new Intent(this, ConfigWifiExecutingActivity.class);
        jumpIntent.putExtras(getIntent());
        jumpIntent.putExtra(IntentConstants.DEVICE_HOTSPOT_SSID, deviceHotspotName);
        jumpIntent.putExtra(IntentConstants.DEVICE_HOTSPOT_PWD, deviceHotspotPwd);
        startActivity(jumpIntent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}