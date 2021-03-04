//package com.example.smarthome.iot.yscamera;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.ClearEditText;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.WifiUtils;
//import com.example.commonlib.base.uiutils.dialog.BaseDialog;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.ScanAddDeviceActivity;
//import com.example.smarthome.iot.entry.DeviceListVo;
//import com.videogo.openapi.EZConstants;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.EZOpenSDKListener;
//import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.xhwl.commonlib.uiutils.ClearEditText;
//import com.xhwl.commonlib.uiutils.LogUtils;
//import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
//import com.xhwl.commonlib.uiutils.wifi.WifiUtils;
//import com.zhy.autolayout.AutoLinearLayout;
//
///**
// * author:
// * date:
// * description: 萤石网络配置
// * update:
// * version:
// */
//public class YscameraNetSettingActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {
//
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private ImageView ys_network_wifi_pwd_no_see;
//
//    private TextView ys_network_change_wifi_name;
//    /**
//     * ChinaNet-2.4G-16AE
//     */
//    private TextView ys_network_wifi_name_tv;
//    /**
//     * 请输入WIFI密码
//     */
//    private ClearEditText ys_network_wifi_pwd;
//    /**
//     * 记住密码
//     */
//    private TextView mGatewayNetSettingWifiOtherRemember;
//    /**
//     * 选择其他网络
//     */
//    private TextView mGatewayNetSettingWifiOtherChoose;
//    /**
//     * 下一步
//     */
//    private Button mGatewayNetSettingWifiNext;
//    private View mTitleLine;
//
//    private BaseDialog mBaseDialog;
//
//    private LinearLayout ys_network_wifi_pwd_no_see_liner,ys_network_wifi_next_linear;
//    private boolean isShow = false;
//    private String serialNo,wifiSSID,wifiPassword,very_code;
//    private boolean  support_Wifi,support_Ap,support_sound_wave;
//    private Bundle bundle;
//    private static final String TAG = YscameraNetSettingActivity.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_network_cfg);
//        addDestoryActivity(this,"YscameraNetSettingActivity");
//        initView();
//        initDate();
//
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (WifiUtils.isWifiEnabled(this)) {
//            ys_network_wifi_name_tv.setText(WifiUtils.getSSID(this));
//        } else {
//            mBaseDialog.show();
//            if (WifiUtils.isWifiEnabled(this)) {
//                //如果已经连接
//                mBaseDialog.dismiss();
//                ys_network_wifi_name_tv.setText(WifiUtils.getSSID(this));
//            }
//        }
//    }
//
//    private void initDate() {
//        mTopTitle.setText("设备配置WI-FI");
//        mTitleLine.setVisibility(View.GONE);
//        showDialog();
//        if (WifiUtils.isWifiEnabled(this)) {
//            ys_network_wifi_name_tv.setText(WifiUtils.getSSID(this));
//        } else {
//            mBaseDialog.show();
//            if (WifiUtils.isWifiEnabled(this)) {
//                //如果已经连接
//                mBaseDialog.dismiss();
//                ys_network_wifi_name_tv.setText(WifiUtils.getSSID(this));
//            }
//        }
//
//        bundle = getIntent().getExtras();
//        serialNo = getIntent().getStringExtra("SerialNo");
//        very_code = getIntent().getStringExtra("very_code");
//        support_Wifi = getIntent().getBooleanExtra("support_Wifi", false);
//        support_Ap = getIntent().getBooleanExtra("support_Ap",false);
//        support_sound_wave = getIntent().getBooleanExtra("support_sound_wave",support_sound_wave);
//        LogUtils.e(TAG,serialNo+"----"+very_code+"==="+support_Wifi+support_Ap+support_sound_wave);//truefalsetrue
//    }
//
//    EZOpenSDKListener.EZStartConfigWifiCallback mEZStartConfigWifiCallback =
//            new EZOpenSDKListener.EZStartConfigWifiCallback() {
//                @Override
//                public void onStartConfigWifiCallback(String deviceSerial, final EZConstants.EZWifiConfigStatus status) {
//                    YscameraNetSettingActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (status == EZConstants.EZWifiConfigStatus.DEVICE_WIFI_CONNECTING) {
//                                LogUtils.e("YscameraNetSettingActivity  DEVICE_WIFI_CONNECTING","DEVICE_WIFI_CONNECTING" + serialNo);
//                            } else if (status == EZConstants.EZWifiConfigStatus.DEVICE_WIFI_CONNECTED) {
//                                LogUtils.e("YscameraNetSettingActivity  DEVICE_WIFI_CONNECTED","Received WIFI on device connection  " + serialNo);
//                            } else if (status == EZConstants.EZWifiConfigStatus.DEVICE_PLATFORM_REGISTED) {
//                                LogUtils.e("YscameraNetSettingActivity  DEVICE_WIFI_CONNECTED","Received PLAT information on device connection " + serialNo);
//                            }
//                        }
//                    });
//                }
//            };
//
//    private void showDialog() {
//        mBaseDialog = new BaseDialog(this,1)
//                .setTitleText("提示")
//                .setInfoText("WIFI未连接，请先连接WIFI")
//                .setCancelVisity(true)
//                .setConfirmListener(this)
//                .setCancelListener(this);
//        mBaseDialog.setCancelable(false);
//    }
//
//    private void initView() {
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//        mTitleLine = (View) findViewById(R.id.title_line);
//
//        ys_network_wifi_name_tv = (TextView) findViewById(R.id.ys_network_wifi_name_tv);
//        ys_network_change_wifi_name = (TextView) findViewById(R.id.ys_network_change_wifi_name);
//        ys_network_change_wifi_name.setOnClickListener(this);
//        ys_network_wifi_pwd = (ClearEditText) findViewById(R.id.ys_network_wifi_pwd);
//        ys_network_wifi_pwd_no_see = findViewById(R.id.ys_network_wifi_pwd_no_see);
//        ys_network_wifi_pwd_no_see_liner = (LinearLayout) findViewById(R.id.ys_network_wifi_pwd_no_see_liner);
//        ys_network_wifi_pwd_no_see_liner.setOnClickListener(this);
//        ys_network_wifi_next_linear = (LinearLayout) findViewById(R.id.ys_network_wifi_next_linear);
//        ys_network_wifi_next_linear.setOnClickListener(this);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.top_back) {
//            finish();
//        } else if (i == R.id.ys_network_change_wifi_name) {//
//            mIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//            startActivity(mIntent);
//        } else if (i == R.id.ys_network_wifi_pwd_no_see_liner) {//
//            if (!isShow) {
//                //设置可查看
//                ys_network_wifi_pwd_no_see.setBackgroundResource(R.drawable.pass_see);
//                ys_network_wifi_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            } else {
//                //设置不能查看
//                ys_network_wifi_pwd_no_see.setBackgroundResource(R.drawable.pass_no_see);
//                ys_network_wifi_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            }
//            isShow = !isShow;
//        } else if (i == R.id.ys_network_wifi_next_linear) {// 下一步
//            wifiSSID = ys_network_wifi_name_tv.getText().toString();
//            wifiPassword = ys_network_wifi_pwd.getText().toString().trim();
//
//            mIntent.setClass(YscameraNetSettingActivity.this,YsAddDevicePercentActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.putExtra("wifiSSID",wifiSSID);
//            mIntent.putExtra("wifiPassword",wifiPassword);
//            mIntent.putExtra("from",TAG);
//            startActivity(mIntent);
////            if (support_Wifi){
////                EZOpenSDK.getInstance().stopConfigWiFi();
////                EZOpenSDK.getInstance().startConfigWifi(YscameraNetSettingActivity.this, serialNo, wifiSSID, wifiPassword,
////                        EZConstants.EZWiFiConfigMode.EZWiFiConfigWave, mEZStartConfigWifiCallback);//EZWiFiConfigSmart  EZWiFiConfigWave
////            }
//        }
//    }
//
//    @Override
//    public void onConfirmClick() {
//        mBaseDialog.dismiss();
//        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
//    }
//
//    @Override
//    public void onCancelListener() {
//        mBaseDialog.dismiss();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EZOpenSDK.getInstance().stopConfigWiFi();
//    }
//}
