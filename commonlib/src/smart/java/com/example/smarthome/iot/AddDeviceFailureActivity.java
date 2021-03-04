package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.impl.device.gateway.activity.ScanAddDeviceActivity_gateway;
import com.xhwl.commonlib.base.BaseActivity;

/**
 * author: glq
 * date: 2019/5/14 15:34
 * description: 添加失败结果
 * update: 2019/5/14
 * version: 
 */
public class AddDeviceFailureActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private View mTitleLine;
    /**
     * 1、请确保WIFI密码正确 \n      （ 如果输错密码，请重置设备后重新添加 ）
     * \n\n2、如果WIFI信号太差 \n      （ 请先将设备、手机、路由器互相靠近后，尝试 重置设备后重新添加 ）
     * \n\n3、请确保已关闭路由器的白名单 \n     （ MAC地址访问限制防蹭网功能后在尝试 ）
     */
    private TextView mDeviceAddFailureInfoText;
    /**
     * 取消
     */
    private TextView mDeviceAddFailureCancel;
    /**
     * 重新添加
     */
    private Button mDeviceAddFailureRestart;

    private String supplierId;
    private DeviceListVo.ProductCollectionBean.DevListBean devBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_failure);
        initView();
        initDate();
    }

    private void initDate() {
        mTitleLine.setVisibility(View.GONE);
        mTopTitle.setText("结果");
        supplierId = getIntent().getStringExtra("supplierId");
        devBean = getIntent().getParcelableExtra("DeviceBean");
    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTitleLine = (View) findViewById(R.id.title_line);
        mDeviceAddFailureInfoText = (TextView) findViewById(R.id.device_add_failure_info_text);
        mDeviceAddFailureCancel = (TextView) findViewById(R.id.device_add_failure_cancel);
        mDeviceAddFailureCancel.setOnClickListener(this);
        mDeviceAddFailureRestart = (Button) findViewById(R.id.device_add_failure_restart);
        mDeviceAddFailureRestart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_back){
            finish();
        } else if(v.getId() == R.id.device_add_failure_cancel){
            finish();
        } else if(v.getId() == R.id.device_add_failure_restart){
            // 重新添加
            mIntent = new Intent(this, ScanAddDeviceActivity.class);
            mIntent.putExtra("DeviceBean",devBean);
            mIntent.putExtra("supplierId",supplierId);
            startActivity(mIntent);
            finish();
        }
    }
}
