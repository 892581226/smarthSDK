package com.example.smarthome.iot.impl.device.cateye.utils;

import android.Manifest;

public class Constants {
    public static final String DISTRIBUTE_URL = "thirdparty.ecamzone.cc";

    /**
     * 申请权限回执常量
     */
    public static final int PERMISSION_APPLY_TOTLE = 110;
    public static final int PERMISSIONS_APPLY_RECORD_WRITE = 111;
    public static final int PERMISSIONS_APPLY_LOCATION = 112;
    public static final int PERMISSIONS_APPLY_CAMERA = 113;
    public static final int PERMISSIONS_APPLY_AUDIO = 114;

    /**
     * 添加设备类型选取
     */
    public static final int SELECT_ADD_DEV_TYPE_DEF = 0;
    public static final int SELECT_ADD_DEV_TYPE_E = 1;
    public static final int SELECT_ADD_DEV_TYPE_D = 2;
    public static final int SELECT_ADD_DEV_TYPE_T21 = 3;
    public static final int SELECT_ADD_DEV_TYPE_H5 = 4;


    /**
     * 网络请求操作类型
     */
    public static final int NETWORK_REQUEST_SETTINGS_DETAILS = 10;
    public static final int NETWORK_REQUEST_STATE_DETAILS = 11;
    public static final int NETWORK_REQUEST_UPDATE_PIR_STATE_SETTINGS = 12;
    public static final int NETWORK_REQUEST_CREATE_TEMPORARY_PWD = 13;
    public static final int NETWORK_REQUEST_LOCK_MSG_LIST = 14;
    public static final int NETWORK_REQUEST_DEL_LOCK_MSG = 15;
    public static final int NETWORK_REQUEST_LOCK_ALARM_LIST = 16;
    public static final int NETWORK_REQUEST_DEL_LOCK_ALARM = 17;

    /**
     * 相机权限
     */
    public static final String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};
    /**
     * 位置、定位权限
     */
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    /**
     * 麦克风、录音权限
     */
    public static final String[] PERMISSIONS_AUDIO = {Manifest.permission.RECORD_AUDIO};

    /**
     * 需要申请的权限数组（读写、定位、录音、相机）
     */
    public static final String[] PERMISSION_GROUP = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA};

    /**
     * 预览类型（0：远程视频 1：语音）
     */
    public static final int REVIEW_TYPE_VIDEO = 0;
    public static final int REVIEW_TYPE_VOICE = 1;

    public static final String APP_KEY = "sdk_demo";
    public static final String DEF_KEYID = "5d91e3b2b7fbb31c";



    public static final int EVENTBUS_DISPATCH_CHANNEL_SETUP_SUCCESSFUL = 111;// 通道建立成功，有数据

    public static final String CALL_OPEN = "open";
}
