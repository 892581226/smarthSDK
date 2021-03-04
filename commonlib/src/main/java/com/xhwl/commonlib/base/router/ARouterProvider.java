package com.xhwl.commonlib.base.router;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface ARouterProvider extends IProvider {
    Activity createActivity();

}
