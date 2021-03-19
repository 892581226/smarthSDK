package com.example.smartsdk;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.base.router.HomeSmartProvider;
import com.xhwl.commonlib.base.router.RouterPath;

public class MainActivity extends BaseActivity {

    private HomeSmartProvider mHomeSmartProvider;
    private Fragment mHomeSmartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String phone = "15909682670";
        mHomeSmartProvider = (HomeSmartProvider) ARouter.getInstance().build(RouterPath.Path_home_smart).navigation();
        mHomeSmartFragment = mHomeSmartProvider.createFragment(phone);
        getSupportFragmentManager().beginTransaction().add(R.id.smart, mHomeSmartFragment).commit();
    }
}