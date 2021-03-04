//package com.example.smarthome.iot.yscamera;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.bumptech.glide.Glide;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.SPUtils;
//import com.example.commonlib.base.uiutils.SpConstant;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.commonlib.base.uiutils.ToastUtil;
//import com.example.commonlib.base.uiutils.progressbar.RoundProgressBar;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.AddDeviceFailureActivity;
//import com.example.smarthome.iot.AddDeviceSuccessActivity;
//import com.example.smarthome.iot.DeviceType;
//import com.example.smarthome.iot.entry.CommonResp;
//import com.example.smarthome.iot.entry.DeviceInfoVo;
//import com.example.smarthome.iot.entry.DeviceListVo;
//import com.example.smarthome.iot.entry.RegistGatewayVo;
//import com.example.smarthome.iot.entry.SmartControlVo;
//import com.example.smarthome.iot.entry.SmartInfoVo;
//import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
//import com.example.smarthome.iot.net.Constant;
//import com.example.smarthome.iot.udpsocket.ReceiveUtils;
//import com.example.smarthome.iot.util.EasySocket;
//import com.example.smarthome.iot.util.SocketCallback;
//import com.lzy.okgo.OkGo;
//import com.lzy.okgo.convert.StringConvert;
//import com.lzy.okgo.model.Response;
//import com.lzy.okrx2.adapter.ObservableResponse;
//import com.videogo.errorlayer.ErrorInfo;
//import com.videogo.exception.BaseException;
//import com.videogo.openapi.EZConstants;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.EZOpenSDKListener;
//import com.videogo.util.ConnectionDetector;
//import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.xhwl.commonlib.constant.SpConstant;
//import com.xhwl.commonlib.uiutils.LogUtils;
//import com.xhwl.commonlib.uiutils.SPUtils;
//import com.xhwl.commonlib.uiutils.StringUtils;
//import com.xhwl.commonlib.uiutils.progressbar.RoundProgressBar;
//import com.zhy.autolayout.AutoLinearLayout;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * author:
// * date:
// * description: 萤石配网，增加设备百分比显示
// * update:
// * version:
// *
// */
//public class YsAddDevicePercentActivity extends BaseActivity implements View.OnClickListener {
//
//    private DatagramPacket packet;
//    private DatagramSocket mSocket;
//    private volatile boolean stopReceiver;
//    private RoundProgressBar mIdProgressBar;
//
//    private final long DURATION_TIME = 80 * 1000;
//    private final int TOTAL_PROGRESS = 100;
//    /**
//     * 0%
//     */
//    private TextView mScanAddDeviceConnectProgress;
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle,ys_add_percent_tv1,ys_add_percent_tv2;
//    private String familyId, supplierId, telephone, devType, getewayId;
//    private DeviceListVo.ProductCollectionBean.DevListBean devBean;
//    private EasySocket socket = null;
//    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
//    private String from,wifiSSID,wifiPassword,serialNo,very_code;
//    private boolean support_Wifi,support_Ap,support_sound_wave;
//    private static final String TAG = YsAddDevicePercentActivity.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_add_percent);
//        addDestoryActivity(this, "YsAddDevicePercentActivity");
//        initView();
//        initDate();
//    }
//
//    private void initView() {
//        mIdProgressBar = (RoundProgressBar) findViewById(R.id.ys_add_percent_progress_bar);
//        mScanAddDeviceConnectProgress = (TextView) findViewById(R.id.ys_add_percent_connect_progress);
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//
//        ys_add_percent_tv1 = findViewById(R.id.ys_add_percent_tv1);
//        ys_add_percent_tv2 = findViewById(R.id.ys_add_percent_tv2);
//    }
//
//    private void initDate() {
//        telephone = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
//        mMsgHandler = new MessageHandler();
//        from = getIntent().getStringExtra("from");
//        serialNo = getIntent().getStringExtra("SerialNo");
//        very_code = getIntent().getStringExtra("very_code");
//        LogUtils.e(TAG,from+"----------");
//        if(!StringUtils.isEmpty(from) && from.equalsIgnoreCase("YscameraNetSettingActivity")){
//            //配网
//            mTopTitle.setText("注册摄像头");
//            ys_add_percent_tv1.setText("正在配置");
//            ys_add_percent_tv2.setText("配网中");
//            Bundle bundle = getIntent().getExtras();
//            wifiSSID = getIntent().getStringExtra("wifiSSID");
//            wifiPassword = getIntent().getStringExtra("wifiPassword");
//
//            support_Wifi = getIntent().getBooleanExtra("support_Wifi", false);
//            support_Ap = getIntent().getBooleanExtra("support_Ap",false);
//            support_sound_wave = getIntent().getBooleanExtra("support_sound_wave",false);
//            LogUtils.e(TAG,serialNo+"----"+very_code+"==="+support_Wifi+support_Ap+support_sound_wave);//truefalsetrue
//
//            EZOpenSDKListener.EZStartConfigWifiCallback mEZStartConfigWifiCallback =
//                    new EZOpenSDKListener.EZStartConfigWifiCallback() {
//                        @Override
//                        public void onStartConfigWifiCallback(String deviceSerial, final EZConstants.EZWifiConfigStatus status) {
//                            YsAddDevicePercentActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (status == EZConstants.EZWifiConfigStatus.DEVICE_WIFI_CONNECTING) {
//                                        LogUtils.e("YscameraNetSettingActivity  DEVICE_WIFI_CONNECTING","DEVICE_WIFI_CONNECTING" + serialNo);
//                                    } else if (status == EZConstants.EZWifiConfigStatus.DEVICE_WIFI_CONNECTED) {
//                                        LogUtils.e("YscameraNetSettingActivity  DEVICE_WIFI_CONNECTED","Received WIFI on device connection  " + serialNo);
//                                        EZOpenSDK.getInstance().stopConfigWiFi();
////                                        addQueryCameraAddVerifyCode(serialNo,very_code);
//                                        ezvizSetDev();
//                                    } else if (status == EZConstants.EZWifiConfigStatus.DEVICE_PLATFORM_REGISTED) {
//                                        LogUtils.e("YscameraNetSettingActivity  DEVICE_WIFI_CONNECTED","Received PLAT information on device connection " + serialNo);
//                                    }
//                                }
//                            });
//                        }
//                    };
//            if (support_Wifi) {
//                EZOpenSDK.getInstance().stopConfigWiFi();
//                EZOpenSDK.getInstance().startConfigWifi(YsAddDevicePercentActivity.this, serialNo, wifiSSID, wifiPassword,
//                        EZConstants.EZWiFiConfigMode.EZWiFiConfigWave, mEZStartConfigWifiCallback);//EZWiFiConfigSmart  EZWiFiConfigWave
//            }
//        } else if(!StringUtils.isEmpty(from) && from.equalsIgnoreCase("YsCameraFoundActivity")){
//            //添加
////            addQueryCameraAddVerifyCode(serialNo,very_code);
//            ezvizSetDev();
//        }
//
//        mTimer.start();
//    }
//
//    /**
//     * 每隔一秒钟执行一次
//     */
//    private CountDownTimer mTimer = new CountDownTimer(DURATION_TIME, 1000) {
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            // 每隔一秒调用一次，剩余多少时间
//            int progress = (int) (TOTAL_PROGRESS * (DURATION_TIME - millisUntilFinished) / DURATION_TIME);
//            mIdProgressBar.setProgress(progress);
//            mScanAddDeviceConnectProgress.setText(progress + "%");
//        }
//
//        @Override
//        public void onFinish() {
//            // 执行完毕
//            mIdProgressBar.setProgress(TOTAL_PROGRESS);
//            mScanAddDeviceConnectProgress.setText(TOTAL_PROGRESS + "%");
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EZOpenSDK.getInstance().stopConfigWiFi();
//        mTimer.cancel();
//        mTimer = null;
//        if (socket != null) {
//            socket.disconnect();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if(v.getId() == R.id.top_back){
//            finish();
//        }
//    }
//
//
//
//    void closeActivity() {
//        destoryActivity("DeviceConnectStepActivity");
//        destoryActivity("YsCaptureActivity");
//        destoryActivity("ManualSnActivity");
//        destoryActivity("YsCameraFoundActivity");
//        destoryActivity("YsCameraNetSettingTipsActivity");
//        destoryActivity("YsAddDevicePercentActivity");
//    }
//
//
//    private void addQueryCameraAddVerifyCode(String devSerial, String verCode) {
//        mTopTitle.setText("绑定账户");
//        ys_add_percent_tv1.setText("正在配置");
//        ys_add_percent_tv2.setText("网络配置成功，正在注册，请耐心等待");
//        // Local network detection
//        if (!ConnectionDetector.isNetworkAvailable(YsAddDevicePercentActivity.this)) {
//            ToastUtil.show(YsAddDevicePercentActivity.this, "添加失败，请检查您的网络");
//            return;
//        }
//
//        new Thread() {
//            public void run() {
//
//                try {
//                    boolean result = EZOpenSDK.getInstance().addDevice(devSerial, verCode);//"137234674", "KRCQDY"
//
//                    LogUtils.e(TAG, result + "----------------");
//                    /***********If necessary, the developer needs to save this code***********/
//                    // 添加成功过后
//                    sendMessage(MSG_ADD_CAMERA_SUCCESS);
//                } catch (BaseException e) {
//                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
//                    LogUtil.debugLog(TAG, errorInfo.toString());
//
//                    sendMessage(MSG_ADD_CAMERA_FAIL, errorInfo.errorCode);
//                    LogUtil.errorLog(TAG, "add camera fail");
//                }
//
//            }
//        }.start();
//    }
//
//    private MessageHandler mMsgHandler = null;
//
//    private void sendMessage(int msgCode, int errorCode) {
//        if (mMsgHandler != null) {
//            Message msg = Message.obtain();
//            msg.what = msgCode;
//            msg.arg1 = errorCode;
//            mMsgHandler.sendMessage(msg);
//        } else {
//            LogUtil.errorLog(TAG, "sendMessage-> mMsgHandler object is null");
//        }
//    }
//
//    private void sendMessage(int msgCode) {
//        if (mMsgHandler != null) {
//            Message msg = Message.obtain();
//            msg.what = msgCode;
//            mMsgHandler.sendMessage(msg);
//        } else {
//            LogUtil.errorLog(TAG, "sendMessage-> mMsgHandler object is null");
//        }
//    }
//
//    private static final int MSG_ADD_CAMERA_SUCCESS = 10;
//
//    private static final int MSG_ADD_CAMERA_FAIL = 12;
//    class MessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what) {
//                case MSG_ADD_CAMERA_SUCCESS:
////                    handleAddCameraSuccess();
//                    LogUtils.e(TAG, "MSG_ADD_CAMERA_SUCCESS-------");
//                    ToastUtil.show(YsAddDevicePercentActivity.this, "增加成功");
//                    if (mTimer != null) {
//                        mTimer.onFinish();
//                        mTimer.cancel();
//                    }
//                    mIntent.setClass(YsAddDevicePercentActivity.this,YsCameraActivity.class);
//                    mIntent.putExtra("fromTag",TAG);
//                    mIntent.putExtra("devSerial",serialNo);
//                    mIntent.putExtra("very_code",very_code);
//                    startActivity(mIntent);
////                    SPUtils.put(YsCameraFoundActivity.this,"ysCamera"+input_sn_cedt1.getText().toString().trim(),input_sn_cedt2.getText().toString().trim());
//                    break;
//                case MSG_ADD_CAMERA_FAIL:
////                    handleAddCameraFail(msg.arg1);
//                    LogUtils.e(TAG, "MSG_ADD_CAMERA_FAIL-------");
//                    if(msg.arg1 == 10031) {
//                        ToastUtil.show(YsAddDevicePercentActivity.this, "子账户或萤石用户没有权限");
//                    } else {
//                        ToastUtil.show(YsAddDevicePercentActivity.this, "增加失败");
//                    }
//                    if (mTimer != null) {
//                        mTimer.cancel();
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }
//
//    private void ezvizSetDev(){
//        JSONObject obj = new JSONObject();
//        obj.put("devId", serialNo);
//        obj.put("deviceSerial", serialNo);
//        obj.put("userId", telephone);
//        obj.put("validateCode", very_code);
//        OkGo.<String>post(Constant.HOST+Constant.Ezviz_SetDev)
//                .upJson(JSON.toJSONString(obj))
//                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
//                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                        LogUtils.e("accept",disposable.isDisposed()+"===");
//                    }
//                })
//                .map(new Function<Response<String>, CommonResp>() {
//                    @Override
//                    public CommonResp apply(Response<String> stringResponse) throws Exception {
//                        LogUtils.e("apply","===="+stringResponse.body());
//                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                        LogUtils.e("resp",resp.getState()+"=====");
//                        return resp;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//
//                .subscribe(new Observer<CommonResp>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                        LogUtils.e("onSubscribe","========---====");
//                    }
//
//                    @Override
//                    public void onNext(CommonResp resp) {
//                        if (resp != null) {
//                            if (resp.getErrorCode().equalsIgnoreCase("200")) {
//                                LogUtils.e(TAG,"onNext  resp 200");
//                                LogUtils.e(TAG, "MSG_ADD_CAMERA_SUCCESS-------");
//                                ToastUtil.show(YsAddDevicePercentActivity.this, "增加成功");
//                                SPUtils.put(YsAddDevicePercentActivity.this,"very_code"+serialNo+telephone,very_code);
//                                if (mTimer != null) {
//                                    mTimer.onFinish();
//                                    mTimer.cancel();
//                                }
//                                closeActivity();
//                                EventBus.getDefault().post("YsCameraRefresh");
//                                mIntent.setClass(YsAddDevicePercentActivity.this,YsCameraActivity.class);
//                                mIntent.putExtra("fromTag",TAG);
//                                mIntent.putExtra("devSerial",serialNo);
//                                mIntent.putExtra("very_code",very_code);
//                                startActivity(mIntent);
//                            } else {
//                                LogUtils.e(TAG,"onNext  resp else");
//                                LogUtils.e(TAG, "MSG_ADD_CAMERA_FAIL-------");
//
//                                if (mTimer != null) {
//                                    mTimer.cancel();
//                                }
//                            }
//                        } else {
//                            LogUtils.e(TAG, "MSG_ADD_CAMERA_FAIL-------");
//
//                            if (mTimer != null) {
//                                mTimer.cancel();
//                            }
//                        }
//                    }
//
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        e.printStackTrace();
//                        LogUtils.e(TAG, "MSG_ADD_CAMERA_FAIL-------onError");
//
//                        if (mTimer != null) {
//                            mTimer.cancel();
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
////                        dismissLoading();
//                        LogUtils.e("onComplete","============");
//                    }
//                });
//    }
//
//}
