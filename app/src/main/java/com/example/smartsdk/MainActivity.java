package com.example.smartsdk;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xhwl.commonlib.base.router.HomeSmartProvider;
import com.xhwl.commonlib.base.router.RouterPath;

public class MainActivity extends AppCompatActivity {

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