package com.example.smarthome.iot.impl.device.gateway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smarthome.R;
import com.example.smarthome.iot.ScanAddDeviceActivity;
import com.example.smarthome.iot.entry.DeviceListVo;
import com.example.smarthome.iot.entry.GateWayBeanVo;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.ClearEditText;
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
import com.xhwl.commonlib.uiutils.wifi.WifiUtils;

import java.io.Serializable;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

/**
 * author: glq
 * date: 2019/5/13 9:37
 * description: 网关网络设置
 * update: 2019/5/13
 * version:
 */
public class GatewayNetSettingActivity_gateway extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mGatewayNetSettingDeviceImg;
    /**
     * 海令智能家居网关
     */
    private TextView mGatewayNetSettingDeviceName;
    /**
     * ChinaNet-2.4G-16AE
     */
    private TextView mGatewayNetSettingWifiName;
    /**
     * 请输入WIFI密码
     */
    private ClearEditText mGatewayNetSettingWifiPassword;
    /**
     * 记住密码
     */
    private TextView mGatewayNetSettingWifiOtherRemember;
    /**
     * 选择其他网络
     */
    private TextView mGatewayNetSettingWifiOtherChoose;
    /**
     * 下一步
     */
    private Button mGatewayNetSettingWifiNext;
    private View mTitleLine;

    private BaseDialog mBaseDialog;
    private Intent mIntent;
    private DeviceListVo.ProductCollectionBean.DevListBean devBean;
    private String supplierId;
    private GateWayBeanVo.GateWayBeanVoData datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_net_setting);
        addDestoryActivity(this,"GatewayNetSettingActivity_gateway");
        initView();
        initDate();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (WifiUtils.isWifiEnabled(this)) {
            mGatewayNetSettingWifiName.setText(WifiUtils.getSSID(this));
        } else {
            mBaseDialog.show();
            if (WifiUtils.isWifiEnabled(this)) {
                //如果已经连接
                mBaseDialog.dismiss();
                mGatewayNetSettingWifiName.setText(WifiUtils.getSSID(this));
            }
        }
    }

    private void initDate() {
//        if (getIntent()!=null) {
//            devBean = getIntent().getParcelableExtra("DeviceBean");
//            supplierId = getIntent().getStringExtra("supplierId");
//        }
        datas = (GateWayBeanVo.GateWayBeanVoData) getIntent().getSerializableExtra("datas");
        if (datas!=null){
            Log.e("连接界面",datas.toString());
        }
        mTopTitle.setText("网络设置");
        mTitleLine.setVisibility(View.GONE);
        Glide.with(this).load(datas.getListPicture()).error(R.drawable.icon_iot_curtain_control_switch).into(mGatewayNetSettingDeviceImg);
//        Glide.with(this).load(devBean.getActualIcon()).error(R.drawable.icon_iot_curtain_control_switch).into(mGatewayNetSettingDeviceImg);
//        mGatewayNetSettingDeviceName.setText(devBean.getDeviceName());
        showDialog();
        if (WifiUtils.isWifiEnabled(this)) {
            mGatewayNetSettingWifiName.setText(WifiUtils.getSSID(this));
        } else {
            mBaseDialog.show();
            if (WifiUtils.isWifiEnabled(this)) {
                //如果已经连接
                mBaseDialog.dismiss();
                mGatewayNetSettingWifiName.setText(WifiUtils.getSSID(this));
            }
        }
    }

    private void showDialog() {
        mBaseDialog = new BaseDialog(this,1)
                .setTitleText("提示")
                .setInfoText("WIFI未连接，请先连接WIFI")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mBaseDialog.setCancelable(false);
    }

    private void initView() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mGatewayNetSettingDeviceImg = (ImageView) findViewById(R.id.gateway_net_setting_device_img);
        mGatewayNetSettingDeviceName = (TextView) findViewById(R.id.gateway_net_setting_device_name);
        mGatewayNetSettingWifiName = (TextView) findViewById(R.id.gateway_net_setting_wifi_name);
        mGatewayNetSettingWifiPassword = (ClearEditText) findViewById(R.id.gateway_net_setting_wifi_password);
        mGatewayNetSettingWifiOtherRemember = (TextView) findViewById(R.id.gateway_net_setting_wifi_other_remember);
        mGatewayNetSettingWifiOtherRemember.setOnClickListener(this);
        mGatewayNetSettingWifiOtherChoose = (TextView) findViewById(R.id.gateway_net_setting_wifi_other_choose);
        mGatewayNetSettingWifiOtherChoose.setOnClickListener(this);
        mGatewayNetSettingWifiNext = (Button) findViewById(R.id.gateway_net_setting_wifi_next);
        mGatewayNetSettingWifiNext.setOnClickListener(this);
        mTitleLine = (View) findViewById(R.id.title_line);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();
        } else if (i == R.id.gateway_net_setting_wifi_other_remember) {// 记住密码
        } else if (i == R.id.gateway_net_setting_wifi_other_choose) {// 选择其他网络
            mIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(mIntent);
        } else if (i == R.id.gateway_net_setting_wifi_next) {// 下一步
            mIntent = new Intent(this, ScanAddDeviceActivity_gateway.class);
//            mIntent.putExtra("DeviceBean", devBean);
//            mIntent.putExtra("supplierId", supplierId);
            mIntent.putExtra("datas",(Serializable) datas);
            startActivity(mIntent);
        }
    }

    @Override
    public void onConfirmClick() {
        mBaseDialog.dismiss();
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
    }

    @Override
    public void onCancelListener() {
        mBaseDialog.dismiss();
    }
}
