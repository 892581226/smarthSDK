package com.example.smarthome.iot.hkcateye.catutil;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.ezviz.sdk.configwifi.EZConfigWifiErrorEnum;
import com.ezviz.sdk.configwifi.EZConfigWifiInfoEnum;
import com.ezviz.sdk.configwifi.EZWiFiConfigManager;
import com.ezviz.sdk.configwifi.ap.ApConfigParam;
import com.ezviz.sdk.configwifi.common.EZConfigWifiCallback;


public class ApConfigWifiPresenter extends ConfigWifiExecutingActivityPresenter {

    public final static String TAG = ApConfigWifiPresenter.class.getSimpleName();

    public ApConfigWifiPresenter(){
        mType = ConfigWifiTypeConstants.CONFIG_WIFI_SDK_AP;
    }

    @Override
    public void startConfigWifi(Application app, Intent configParam) {
        // step1.准备配网参数
        ApConfigParam param = new ApConfigParam();
        param.routerWifiSsid /*路由器wifi名称*/= configParam.getStringExtra(IntentConstants.ROUTER_WIFI_SSID);
        param.routerWifiPwd /*路由器wifi密码*/= configParam.getStringExtra(IntentConstants.ROUTER_WIFI_PASSWORD);
        param.deviceSerial /*设备序列号*/= configParam.getStringExtra(IntentConstants.DEVICE_SERIAL);
        param.deviceVerifyCode /*设备验证码*/= configParam.getStringExtra(IntentConstants.DEVICE_VERIFY_CODE);
        param.deviceHotspotSsid /*设备热点名称*/= configParam.getStringExtra(IntentConstants.DEVICE_HOTSPOT_SSID);
        param.deviceHotspotPwd /*设备热点密码*/= configParam.getStringExtra(IntentConstants.DEVICE_HOTSPOT_PWD);
        param.autoConnect /*是否自动连接到设备热点*/= !configParam.getBooleanExtra(IntentConstants.USE_MANUAL_AP_CONFIG,
                false);
        Log.e(TAG, "startConfigWifi: "+"--" +param.routerWifiSsid +"--"+param.routerWifiPwd
                +"--"+ param.deviceSerial+"--"+ param.deviceVerifyCode+"--"+param.autoConnect+
                "--"+param.deviceHotspotSsid +"--"+param.deviceHotspotPwd);
        // step2.开始配网
        EZWiFiConfigManager.startAPConfig(app, param, new EZConfigWifiCallback(){
            @Override
            public void onInfo(int code, String message) {
                super.onInfo(code, message);
                Log.e(TAG, "onInfo: "+code );
                if (mCallback == null){
                    return;
                }
                // step3.结束配网
                if (code == EZConfigWifiInfoEnum.CONNECTING_SENT_CONFIGURATION_TO_DEVICE.code){
                    // todo 提示用户配网成功
                    //mCallback.onConnectedToWifi();
                    // 配网成功后，需要停止配网
                    //EZWiFiConfigManager.stopAPConfig();
                }
                if (code == EZConfigWifiInfoEnum.CONNECTING_TO_WIFI.code){
                    // todo 提示用户配网成功
                    mCallback.onConnectedToWifi();
                    // 配网成功后，需要停止配网
                    EZWiFiConfigManager.stopAPConfig();
                }
            }
            @Override
            public void onError(int code, String description) {
                super.onError(code, description);
                if (mCallback == null){
                    return;
                }
                mCallback.onConfigError(code, description);

                // step3.结束配网
                if (code == EZConfigWifiErrorEnum.CONFIG_TIMEOUT.code){
                    // todo 提示用户配网超时
                    mCallback.onTimeout();
                    // 配网失败后，需要停止配网
                    EZWiFiConfigManager.stopAPConfig();
                }else if(code == EZConfigWifiErrorEnum.MAY_LACK_LOCATION_PERMISSION.code){
                    // todo 提示用户授予app定位权限，并打开定位开关
                }else if (code == EZConfigWifiErrorEnum.WRONG_DEVICE_VERIFY_CODE.code){
                    // todo 提示用户验证码输入错误
                }
            }
        });
    }

    @Override
    public void stopConfigWifi() {
        mCallback = null;
        EZWiFiConfigManager.stopAPConfig();
    }

}
