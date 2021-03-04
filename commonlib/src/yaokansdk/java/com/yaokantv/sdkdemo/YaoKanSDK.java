package com.yaokantv.sdkdemo;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.yaokantv.yaokansdk.callback.YaokanSDKListener;
import com.yaokantv.yaokansdk.manager.Yaokan;
import com.yaokantv.yaokansdk.model.Operators;
import com.yaokantv.yaokansdk.model.YkMessage;
import com.yaokantv.yaokansdk.model.e.MsgType;

public class YaoKanSDK {

    private static final YaoKanSDK mInstance = new YaoKanSDK();
    public static String curMac = "";//设备MAC
    public static String curDid = "";//设备DID
    public static String curRf = "0";//设备是否支持射频
    public static String curTName = "";//设备类型
    public static String curBName = "";//品牌名称
    public static int curTid = 0;//设备类型ID
    public static int curBid = 0;//品牌ID
    public static int curGid = 0;//组ID
    public static Operators operators = null;//运营商对象
    String appId = "7ac1f859045fd673f1d720b86b49f691";
    String appSecret = "96a5a606fae5cf4a942c8545021051dc";
    private boolean isUi=true;

    public static synchronized YaoKanSDK instance() {
        return mInstance;
    }
/*
    public void init(Application context) {
        Hawk.init(context).build();
        Yaokan.initialize(context);
        Yaokan.instance().init(context, appId, appSecret).addSdkListener(new YaokanSDKListener() {
            @Override
            public void onReceiveMsg(MsgType msgType, YkMessage ykMessage) {

            }
        });

    }
    public void onTerminate(Application application) {
        Yaokan.instance().onTerminate(application);
    }
*/
}