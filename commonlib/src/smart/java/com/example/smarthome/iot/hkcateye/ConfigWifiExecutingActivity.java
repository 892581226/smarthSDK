package com.example.smarthome.iot.hkcateye;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.hkcateye.catutil.ConfigWifiExecutingActivityPresenter;
import com.example.smarthome.iot.hkcateye.catutil.ConfigWifiTypeConstants;
import com.example.smarthome.iot.hkcateye.catutil.IntentConstants;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;
import com.example.smarthome.iot.hkcateye.catutil.RouteNavigator;
import com.example.smarthome.iot.hkcateye.catutil.WifiUtil;
import com.example.smarthome.iot.net.Constant;
import com.ezviz.sdk.configwifi.EZConfigWifiErrorEnum;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;
import static com.xhwl.commonlib.application.MyAPP.destoryActivity;
import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;

public class ConfigWifiExecutingActivity extends RootActivity implements ConfigWifiExecutingActivityPresenter.Callback {
    public final static String TAG = ConfigWifiExecutingActivity.class.getSimpleName();
    private ConfigWifiExecutingActivityPresenter mPresenter;
    private RelativeLayout mConfigResultView;
    private LinearLayout mConfigSuccessView;
    private LinearLayout mConfigFailView;
    private TextView mConfigErrorInfoTv;
    private String mAllErrorInfo;
    private String mHKToken;
    private ConnectivityManager manager;
    private ZLoadingDialog loadingDialog;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 200:
                    destoryActivity("AutoWifiNetConfigActivity");
                    destoryActivity("AutoWifiPrepareStepOneActivity");
                    destoryActivity("AutoWifiResetActivity");
                    destoryActivity("ManualChooseConfigWifiWayActivity");
                    destoryActivity("ManualInputDeviceHotspotInfoActivity");
                    destoryActivity("NoBindHYCatEyeActivity");
                    destoryActivity("YsCaptureActivity");
                    EventBus.getDefault().post(new UpdateFamilyEvent(true));
                    ToastUtil.show("添加成功");
                    finish();
                    break;
            }
        }
    };
    public static void launch(Context context, Intent intent){
        Intent newIntent = new Intent(context, ConfigWifiExecutingActivity.class);
        newIntent.putExtras(intent);
        newIntent.putExtra(IntentConstants.USE_MANUAL_AP_CONFIG, true);
        context.startActivity(newIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_wifi_executing);
        addDestoryActivity(ConfigWifiExecutingActivity.this,"ConfigWifiExecutingActivity");
        mPresenter = ConfigWifiExecutingActivityPresenter.getPresenter(getIntent()
                .getStringExtra(IntentConstants.SELECTED_PRESENTER_TYPE));
        if (mPresenter == null){
            return;
        }
        loadingDialog = new ZLoadingDialog(this);
        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
                .setLoadingColor(Color.BLUE)//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setCanceledOnTouchOutside(false);
        WifiUtil.getIns().init(getApplicationContext());
        //得到网络连接信息
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        initUI();
        showExecutingUi();
        mHKToken = getIntent().getStringExtra(IntentConstants.TOKEN);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                startConfig();
            }
        });
        WifiUtil.getIns().changeToWifi(getIntent().getStringExtra(IntentConstants.DEVICE_HOTSPOT_SSID),
                getIntent().getStringExtra(IntentConstants.DEVICE_HOTSPOT_PWD));

    }
    @Override
    protected void onStop() {
        super.onStop();
        /*showToast(getString(R.string.app_common_stop_config_wifi_while_switched_to_background));
        exitPage();*/
    }

    /**
     * 重试
     */
    public void onClickRetryConfigWifi(View view) {
        showExecutingUi();
        startConfig();
    }

    /**
     * 配网成功，根据使用sdk不同，跳转到对应页面
     */
    public void onClickToConfigAnother(View view) {
        String familyId = SPUtils.get(ConfigWifiExecutingActivity.this, "FamilyId", "");
        String deviceSerial = getIntent().getStringExtra(IntentConstants.DEVICE_SERIAL);
        String deviceCode = getIntent().getStringExtra(IntentConstants.DEVICE_VERIFY_CODE);
        boolean flag = false;
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            //setNetwork();
            ToastUtil.show("无网络");
        } else {
            saveDev(deviceSerial, familyId, deviceCode);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //exitPage();
    }

    public void onClickExit(View view) {
        finish();
        exitPage();
    }

    private void exitPage(){
        stopConfig();
       // finish();
    }

    @Override
    public void onConnectedToWifi() {
        // 仅使用配网sdk时，才展示配网成功的ui
        WifiUtil.getIns().changeToWifi(getIntent().getStringExtra(IntentConstants.ROUTER_WIFI_SSID),
                getIntent().getStringExtra(IntentConstants.ROUTER_WIFI_PASSWORD));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showConfigSuccessUi();
            }
        },2000);

    }

    private void saveDev(String deviceSerial,String familyId,String deviceCode) {
        loadingDialog.show();
        JSONObject obj = new JSONObject();
        obj.put("accessToken",mHKToken);
        obj.put("dev_id",deviceSerial);
        obj.put("supplier","海康电子猫眼");
        obj.put("deviceSerial", deviceSerial);
        obj.put("ownFamily", familyId);
        obj.put("validateCode", deviceCode);
        OkGo.<String>post(Constant.HOST + Constant.HKAdd)
                .upJson(JSON.toJSONString(obj))
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp", resp.getState() + "=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {

                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                            loadingDialog.dismiss();
                            handler.sendEmptyMessage(200);
                        }else{
                            loadingDialog.dismiss();
                            if (resp.getMessage().contains("设备已经被添加")){
                                ToastUtil.show(resp.getMessage());
                            }else {
                                ToastUtil.show(resp.getMessage());
                            }
                        }

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });

    }

    @Override
    public void onConnectedToPlatform() {
        ARouter.getInstance().build(RouteNavigator.ADD_DEVICE_PAGE)
                .withString(IntentConstants.DEVICE_SERIAL, getIntent().getStringExtra(IntentConstants.DEVICE_SERIAL))
                .withString(IntentConstants.DEVICE_VERIFY_CODE, getIntent().getStringExtra(IntentConstants.DEVICE_VERIFY_CODE))
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation(this, new NavCallback() {
                    @Override
                    public void onArrival(Postcard postcard) {
                        exitPage();
                    }

                    @Override
                    public void onLost(Postcard postcard) {
                        // do nothing
                    }
                });
    }

    @Override
    public void onConfigInfo(int info) {

    }

    @Override
    public void onConfigError(int code, String msg) {
        String errorInfo = "***"  + "error code is: " + code +  ", error msg is: " + msg + "\n";
        // 仅展示新定义的错误码
        for (EZConfigWifiErrorEnum error : EZConfigWifiErrorEnum.values()){
            if (code == error.code){
                if (mAllErrorInfo == null){
                    mAllErrorInfo = "" + errorInfo;
                }else{
                    mAllErrorInfo += errorInfo;
                }
                break;
            }
        }
        // 用点则认为配网户拒绝连接设备热失败
        if (code == EZConfigWifiErrorEnum.USER_REFUSED_CONNECTION_REQUEST.code){
            failedToConfig();
        }
    }

    @Override
    public void onTimeout() {
        failedToConfig();
    }

    private void failedToConfig(){
        stopConfig();
        switch (mPresenter.getType()){
            case ConfigWifiTypeConstants.CONFIG_WIFI_SDK_AP:
                showConfigFailUi();
                break;
            case ConfigWifiTypeConstants.FULL_SDK_AP:
                //ManualConnectDeviceHotspotActivity.Companion.launch(this, getIntent());

                break;
            default:
                showConfigFailUi();
                break;
        }
    }

    private void startConfig(){
        if (mPresenter != null){
            mPresenter.setCallback(this);
            mPresenter.startConfigWifi(getApplication(), getIntent());
        }
    }

    private void stopConfig(){
        if (mPresenter != null){
            mPresenter.setCallback(null);
            mPresenter.stopConfigWifi();
        }
    }

    private void initUI() {
        mConfigResultView = findViewById(R.id.app_common_vg_config_result);
        mConfigSuccessView = findViewById(R.id.app_common_config_result_success);
        mConfigFailView = findViewById(R.id.app_common_config_result_fail);
        //mConfigErrorInfoTv = findViewById(R.id.app_common_all_config_error_info);
    }

    private void showExecutingUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConfigResultView.setVisibility(View.GONE);
                mConfigSuccessView.setVisibility(View.GONE);
                mConfigFailView.setVisibility(View.GONE);
                mAllErrorInfo = null;
            }
        });
    }

    private void showConfigSuccessUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConfigResultView.setVisibility(View.VISIBLE);
                mConfigSuccessView.setVisibility(View.VISIBLE);
                mConfigFailView.setVisibility(View.GONE);
                mAllErrorInfo = null;
            }
        });
    }

    private void showConfigFailUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConfigErrorInfoTv.setText(mAllErrorInfo);
                mConfigResultView.setVisibility(View.VISIBLE);
                mConfigSuccessView.setVisibility(View.GONE);
                mConfigFailView.setVisibility(View.VISIBLE);
                mAllErrorInfo = null;
            }
        });
    }

}