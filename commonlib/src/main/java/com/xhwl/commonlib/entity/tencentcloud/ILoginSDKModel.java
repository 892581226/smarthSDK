package com.xhwl.commonlib.entity.tencentcloud;

/**
 * Created by stephon on 2018/7/31.
 */

public interface ILoginSDKModel {

    interface onLoginSDKListener {
        void onLoginSDKSuccess(String userId);

        void onLoginSDKFailed(String module, int errCode, String errMsg);
    }

    interface onLogoutSDKListener {
        void onLogoutSDKSuccess(boolean reLoad, Object data);

        void onLogoutSDKFailed(String module, int errCode, String errMsg);
    }

    void loginSDK(String userId, String userSig, onLoginSDKListener listener);

    void logoutSDK(boolean reLoad, onLogoutSDKListener listener);
}
