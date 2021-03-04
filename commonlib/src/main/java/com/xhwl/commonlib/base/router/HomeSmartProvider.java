package com.xhwl.commonlib.base.router;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface HomeSmartProvider extends IProvider {
    Fragment createFragment(String userId);

    void initYsSdk(Application application, String appKey);

    void logoutYsSdk();
}
