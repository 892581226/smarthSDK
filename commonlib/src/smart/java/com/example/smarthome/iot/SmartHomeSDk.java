package com.example.smarthome.iot;

import android.content.Context;

public class SmartHomeSDk {
    private static final String TAG="SmartHomeSDk";
    private Context mApplicationContext;

    public SmartHomeSDk() {
    }


    /**
     * 初始化sdk
     * @param context
     */
    public void init(Context context){
        mApplicationContext=context.getApplicationContext();
    }

    /**
     * 获取应用上下文
     * @return
     */
    public Context getContext(){
        return mApplicationContext;
    }


    private static class SmartHomeHolder{
        private static SmartHomeSDk instance=new SmartHomeSDk();
    }

    public static SmartHomeSDk getInstance(){
        return SmartHomeHolder.instance;
    }
}
