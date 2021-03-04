package com.xhwl.commonlib.base.router;

import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface HomeFindProvider extends IProvider {
    Fragment createFragment();

}
