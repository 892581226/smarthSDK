package com.example.smarthome.iot.router;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.example.smarthome.iot.HomeSmartFragment;
//import com.videogo.openapi.EZOpenSDK;
import com.xhwl.commonlib.base.router.HomeSmartProvider;
import com.xhwl.commonlib.base.router.RouterPath;

@Route(path = RouterPath.Path_home_smart)
public class HomeSmartProviderImpl implements HomeSmartProvider {
    @Override
    public Fragment createFragment(String userId) {
        HomeSmartFragment fragment = HomeSmartFragment.newInstance(userId);
        return fragment;
    }

    @Override
    public void initYsSdk(Application application,String appKey) {
        //萤石
//        /** * sdk日志开关，正式发布需要去掉 */
//        EZOpenSDK.showSDKLog(true);
//        /** * 设置是否支持P2P取流,详见api */
//        EZOpenSDK.enableP2P(false);
//        /** * APP_KEY请替换成自己申请的 */
//        EZOpenSDK.initLib(application, appKey);//e24bec0228ad4f41bf7f0586d23d9ebe
    }

    @Override
    public void logoutYsSdk() {
//        EZOpenSDK.getInstance().logout();
    }

    @Override
    public void init(Context context) {

    }
}
