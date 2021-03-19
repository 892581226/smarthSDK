package com.xhwl.commonlib.base.router;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface HomeFindProvider extends IProvider {
    Fragment createFragment();

}
