package com.example.smarthome.iot.hkcateye;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.hkcateye.catutil.ConfigWifiTypeConstants;
import com.example.smarthome.iot.hkcateye.catutil.IntentConstants;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;


public class ManualChooseConfigWifiWayActivity extends RootActivity implements View.OnClickListener {
    private final String TAG = ManualChooseConfigWifiWayActivity.class.getSimpleName();
    private String mEzvizDeviceHotspotPrefix = "EZVIZ_";
    private int REQUEST_CODE_SCAN = 100;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_choose_config_wifi_way);
        addDestoryActivity(this,"ManualChooseConfigWifiWayActivity");
        //initTitleBar();
        initUi();
    }

    protected void initUi() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("配置wifi");
        showAvailableConfigWays();
    }

    private void showAvailableConfigWays() {
        boolean isUsingFullSdk = getIntent().getBooleanExtra(IntentConstants.USING_CONFIG_WIFI_SDK, false);
        Button toApView = findViewById(R.id.btn_to_ap);
        if (toApView != null){
            if (isUsingFullSdk){
                toApView.setVisibility(getIntent().getBooleanExtra(IntentConstants.EXTRA_SUPPORT_AP, false) ? View.VISIBLE : View.GONE);
            }
            toApView.setOnClickListener(mChooseConfigWifiListener);
        }
    }

    private View.OnClickListener mChooseConfigWifiListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent toConfigIntent = new Intent();
            toConfigIntent.putExtras(getIntent());
            String presenterType = null;
            int id = v.getId();
            if (id == R.id.btn_to_ap){
                presenterType = ConfigWifiTypeConstants.CONFIG_WIFI_SDK_AP;
                toConfigIntent.setClass(ManualChooseConfigWifiWayActivity.this, ConfigWifiExecutingActivity.class);
                String deviceHotspotName = mEzvizDeviceHotspotPrefix + getIntent().getStringExtra(IntentConstants.DEVICE_SERIAL);
                String deviceHotspotPwd = mEzvizDeviceHotspotPrefix + getIntent().getStringExtra(IntentConstants.DEVICE_VERIFY_CODE);
                toConfigIntent.putExtra(IntentConstants.DEVICE_HOTSPOT_SSID, deviceHotspotName);
                toConfigIntent.putExtra(IntentConstants.DEVICE_HOTSPOT_PWD, deviceHotspotPwd);
                toConfigIntent.putExtra(IntentConstants.SELECTED_PRESENTER_TYPE, presenterType);
                //toConfigIntent.setComponent(componentName);
                startActivity(toConfigIntent);
                /*else{
                    presenterType = ConfigWifiTypeConstants.FULL_SDK_AP;
                    componentName = new ComponentName(mContext, ManualInputDeviceHotspotInfoActivity.class);
                }*/
            }/*else if(id == R.id.btn_to_smart_config){
                if (getIntent().getBooleanExtra(IntentConstants.USING_CONFIG_WIFI_SDK, false)){
                    presenterType = ConfigWifiTypeConstants.CONFIG_WIFI_SDK_SMART_CONFIG;
                }else{
                    presenterType = ConfigWifiTypeConstants.FULL_SDK_SMART_CONFIG;
                }
                componentName = new ComponentName(mContext, ConfigWifiExecutingActivity.class);
                toConfigIntent.putExtra(IntentConstants.EXTRA_SUPPORT_SOUND_WAVE, false);
            }else if(id == R.id.btn_to_sound_wave){
                if (getIntent().getBooleanExtra(IntentConstants.USING_CONFIG_WIFI_SDK, false)){
                    presenterType = ConfigWifiTypeConstants.CONFIG_WIFI_SDK_SOUND_WAVE;
                }else{
                    presenterType = ConfigWifiTypeConstants.FULL_SDK_SOUND_WAVE;
                }
                componentName = new ComponentName(mContext, ConfigWifiExecutingActivity.class);
                toConfigIntent.putExtra(IntentConstants.EXTRA_SUPPORT_SMART_CONFIG, false);
            }*//*else{
                toConfigIntent = null;
            }*/

        }

    };

    @Override
    public void onClick(View v) {
        finish();
    }

 /*   private void initTitleBar() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        if (titleBar != null){
            titleBar.setTitle(getString(R.string.choose_config_wifi_way));
            titleBar.addBackButton(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }*/
}