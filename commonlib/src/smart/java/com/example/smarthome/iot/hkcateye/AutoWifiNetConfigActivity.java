package com.example.smarthome.iot.hkcateye;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.hkcateye.catutil.ConfigWifiTypeConstants;
import com.example.smarthome.iot.hkcateye.catutil.IntentConstants;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;
import com.example.smarthome.iot.net.Constant;
import com.ezviz.sdk.configwifi.WiFiUtils;
import com.ezviz.sdk.configwifi.ap.ConnectionDetector;
import com.hikvision.wifi.configuration.BaseUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.uiutils.LogUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class AutoWifiNetConfigActivity extends RootActivity implements View.OnClickListener {
    public static final String DEVICE_TYPE = "device_type";
    public static final String WIFI_PASSWORD = "wifi_password";
    public static final String WIFI_SSID = "wifi_ssid";
    private Button btnNext;
    private TextView tvSSID;
    private EditText edtPassword;
    private TextView tvTitle;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private String presenterType;
    private String mHKToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_wifi_net_config);
        addDestoryActivity(this,"AutoWifiNetConfigActivity");
        initTitleBar();
        findViews();
        initUI();

    }

    private void getHKToken() {
        OkGo.<String>get(Constant.HOST + Constant.GetToken)
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, CommonResp>() {

                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CommonResp resp) {

                        JSONObject jsonObject = JSON.parseObject(resp.getResult());
                        mHKToken = jsonObject.getString("accessToken");
                        if (mHKToken !=null){
                            goToChooseConfigWifiWay();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.toString() );
                    }

                    @Override
                    public void onComplete() {

                    }
                });



    }

    private void initTitleBar() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText(R.string.auto_wifi_cer_config_title1);
    }

    private void showWifiRequiredDialog() {

        new AlertDialog.Builder(this).setTitle(R.string.auto_wifi_dialog_title_wifi_required)
                .setMessage(R.string.please_open_wifi_network)
                .setNegativeButton(R.string.auto_wifi_dialog_btn_wifi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {

                        dialog.dismiss();
                        // 跳转wifi设置界面
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                }).setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        }).setCancelable(false).create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectionDetector.getConnectionType(this) != ConnectionDetector.WIFI) {
            tvSSID.setText(R.string.unknow_ssid);
            showWifiRequiredDialog();
        } else {
            updateWifiInfo();
        }
    }

    private void updateWifiInfo(){
        // 优先使用getCurrentWifiSsid方法获取wifi名
        String wifiName = WiFiUtils.getCurrentWifiSsid(this);
        // 如上述方式无效，则使用getWifiSSID方法进行获取
        if (!isValidWifiSSID(wifiName)){
            wifiName = BaseUtil.getWifiSSID(this);
        }
        if (isValidWifiSSID(wifiName)){
            tvSSID.setText(wifiName);
        }
    }

    private boolean isValidWifiSSID(String wifiName){
        return !TextUtils.isEmpty(wifiName) && !"<unknown ssid>".equalsIgnoreCase(wifiName);
    }

    private void findViews() {
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHKToken();
            }
        });
        tvSSID = findViewById(R.id.tvSSID);
        edtPassword = findViewById(R.id.edtPassword);
    }


    private void initUI() {
        mTopTitle.setText(R.string.auto_wifi_cer_config_title2);
        String password = "";
        edtPassword.setText(password);
    }



    private void goToChooseConfigWifiWay(){
        Intent toConfigIntent = new Intent(mContext, ConfigWifiExecutingActivity.class);
        toConfigIntent.putExtras(getIntent());
        toConfigIntent.putExtra(IntentConstants.ROUTER_WIFI_SSID, tvSSID.getText().toString());
        toConfigIntent.putExtra(IntentConstants.ROUTER_WIFI_PASSWORD, TextUtils.isEmpty(edtPassword.getText().toString())
                ? "smile" : edtPassword.getText().toString());
        presenterType = ConfigWifiTypeConstants.CONFIG_WIFI_SDK_AP;
        String mEzvizDeviceHotspotPrefix = "";
        if (!getIntent().getStringExtra("devCode").isEmpty()){
            if (getIntent().getStringExtra("devCode").contains("CS-DP1C-4A1WPFBSR")){
                mEzvizDeviceHotspotPrefix = "EZVIZ_";
            }else {
                mEzvizDeviceHotspotPrefix = "SoftAP_";
            }
        }
        Log.e("TAG", "goToChooseConfigWifiWay: "+mEzvizDeviceHotspotPrefix );
        String deviceHotspotName = mEzvizDeviceHotspotPrefix + getIntent().getStringExtra(IntentConstants.DEVICE_SERIAL);
        String deviceHotspotPwd = mEzvizDeviceHotspotPrefix + getIntent().getStringExtra(IntentConstants.DEVICE_VERIFY_CODE);
        toConfigIntent.putExtra(IntentConstants.DEVICE_HOTSPOT_SSID, deviceHotspotName);
        toConfigIntent.putExtra(IntentConstants.DEVICE_HOTSPOT_PWD, deviceHotspotPwd);
        toConfigIntent.putExtra(IntentConstants.SELECTED_PRESENTER_TYPE, presenterType);
        toConfigIntent.putExtra(IntentConstants.TOKEN, mHKToken);
        startActivity(toConfigIntent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}