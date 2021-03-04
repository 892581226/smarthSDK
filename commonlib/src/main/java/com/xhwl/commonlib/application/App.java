package com.xhwl.commonlib.application;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.smarthome.BuildConfig;
import com.orhanobut.hawk.Hawk;
import com.tencent.smtt.sdk.QbSdk;
import com.yaokantv.yaokansdk.manager.Yaokan;

public class App  extends Application {
    String appId = "7ac1f859045fd673f1d720b86b49f691";
    String appSecret = "96a5a606fae5cf4a942c8545021051dc";
    @Override
    public void onCreate() {
        super.onCreate();
        MyAPP.instance().init(this);
        initSmartSdk();
        X5WebView();
    }

    private void X5WebView() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    private void initSmartSdk() {
        if (BuildConfig.DEBUG){
            //这2个必须要在初始化之前开启。These two lines must be written before init, otherwise these configurations will be //invalid in the init process
            ARouter.openLog();
            ARouter.openDebug();
            //  Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version //needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this);   //初始化SDK   As early as possible, it is recommended to initialize in the
        Yaokan.initialize(this);
        Hawk.init(this).build();
        MultiDex.install(this);
        Yaokan.instance().init(this, appId, appSecret);
        MyAPP.instance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Yaokan.instance().onTerminate(this);
    }
}
