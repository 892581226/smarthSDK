package com.xhwl.commonlib.entity.tencentcloud.impl;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.xhwl.commonlib.entity.tencentcloud.ILoginSDKModel;

/**
 * Created by stephon on 2018/7/31.
 */

public class LoginSDKSDKModelImpl implements ILoginSDKModel {

    @Override
    public void loginSDK(final String userId, String userSig, final ILoginSDKModel.onLoginSDKListener listener) {
        ILiveLoginManager.getInstance().iLiveLogin(userId, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                listener.onLoginSDKSuccess(userId);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                listener.onLoginSDKFailed(module, errCode, errMsg);
            }
        });


    }

    @Override
    public void logoutSDK(final boolean reLoad, final onLogoutSDKListener listener) {
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                listener.onLogoutSDKSuccess(reLoad,data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                listener.onLogoutSDKFailed(module, errCode, errMsg);
            }
        });
    }

}
