//package com.example.smarthome.iot.yscamera;
//
//import android.app.Application;
//
//import com.videogo.openapi.EZOpenSDK;
//
//public class InitYssdkHelper {
//    public static void initYssdk(Application application,String appKey){
//        //萤石
//        /** * sdk日志开关，正式发布需要去掉 */
//        EZOpenSDK.showSDKLog(true);
//        /** * 设置是否支持P2P取流,详见api */
//        EZOpenSDK.enableP2P(false);
//        /** * APP_KEY请替换成自己申请的 */
//        EZOpenSDK.initLib(application, appKey);//e24bec0228ad4f41bf7f0586d23d9ebe
//
//    }
//
//    public static void logoutYssdk(){
//        EZOpenSDK.getInstance().logout();
//    }
//}
