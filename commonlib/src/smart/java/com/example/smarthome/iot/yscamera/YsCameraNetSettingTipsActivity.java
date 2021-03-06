//package com.example.smarthome.iot.yscamera;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.commonlib.base.uiutils.ToastUtil;
//import com.example.commonlib.base.uiutils.dialog.BaseDialog;
//import com.example.commonlib.base.uiutils.dialog.BaseEditDialog;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.entry.SubUserListVo;
//import com.videogo.errorlayer.ErrorInfo;
//import com.videogo.exception.BaseException;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.bean.EZDeviceInfo;
//import com.videogo.openapi.bean.EZProbeDeviceInfoResult;
//import com.videogo.util.ConnectionDetector;
//import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.xhwl.commonlib.uiutils.LogUtils;
//import com.xhwl.commonlib.uiutils.ToastUtil;
//import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
//import com.zhy.autolayout.AutoLinearLayout;
//import com.zyao89.view.zloading.ZLoadingDialog;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;
//import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;
//
///**
// * 添加智能摄像头 配网tips
// */
//public class YsCameraNetSettingTipsActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {
//
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private TextView mTopBtn;
//    private ImageView mTopRecord;
//
//
//    private String deviceType, serialNo, verifyCode;
//
//
//    private Button device_connect_step_next_btn;
//    private Bundle bundle;
//    private static final String TAG = YsCameraNetSettingTipsActivity.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_add_camera_tip);
//        addDestoryActivity(this,TAG);
//        initView();
//        initDate();
//    }
//
//    private void initDate() {
//        mTopTitle.setText("添加智能摄像头");
////        Bundle bundle = getIntent().getExtras();
////        if(bundle!=null){
////            serialNo = bundle.getString("SerialNo");
////            verifyCode = bundle.getString("very_code");
////            deviceType = bundle.getString("DeviceType");
////            LogUtils.e(TAG,serialNo+"----"+verifyCode+"==="+deviceType);
////        }
////        if(!StringUtils.isEmpty(serialNo)){
////            getYsCameraInfo(serialNo,"");
////            getDeviceInfo(serialNo);
////        } else {
////            ToastUtil.show(YsCameraNetSettingTipsActivity.this,"设备序列号为空");
////        }
//
//        bundle = getIntent().getExtras();
//        LogUtils.e(TAG, bundle.getString("SerialNo") + "=============");
//    }
//
//    private void initView() {
//
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//        mTopBtn = (TextView) findViewById(R.id.top_btn);
//        mTopBtn.setOnClickListener(this);
//        mTopBtn.setVisibility(View.GONE);
//        mTopRecord = (ImageView) findViewById(R.id.top_record);
//
//
//        device_connect_step_next_btn = findViewById(R.id.device_connect_step_next_btn);
//        device_connect_step_next_btn.setOnClickListener(this);
//
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.top_back) {
//            finish();
//        } else if (i == R.id.top_btn) {
//
//        } else if (i == R.id.device_connect_step_next_btn) {//
//            mIntent.setClass(YsCameraNetSettingTipsActivity.this, YscameraNetSettingActivity.class);
////                mIntent.putExtra("SerialNo", serialNo);
////                mIntent.putExtra("very_code", verifyCode);
////                if (mEZProbeDeviceInfo != null && mEZProbeDeviceInfo.getEZProbeDeviceInfo() != null) {
////                    if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportWifi() == 3) {
////                        mIntent.putExtra("support_Wifi", true);
////                    }
////                    if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportAP() == 2) {
////                        mIntent.putExtra("support_Ap", true);
////                    }
////                    if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportSoundWave() == 1) {
////                        mIntent.putExtra("support_sound_wave", true);
////                    }
////                } else {
////                    mIntent.putExtra("support_unknow_mode", true);
////                }
//////                    mIntent.putExtra("device_type", mDeviceType);
//            mIntent.putExtras(bundle);
//            startActivity(mIntent);
//
//        }
//    }
//
//
//    @Override
//    public void onConfirmClick() {
//
//    }
//
//    @Override
//    public void onCancelListener() {
//
//    }
//
//    private void addQueryCameraAddVerifyCode(String devSerial, String verCode) {
//
//        // Local network detection
//        if (!ConnectionDetector.isNetworkAvailable(YsCameraNetSettingTipsActivity.this)) {
//            ToastUtil.show(YsCameraNetSettingTipsActivity.this, "添加失败，请检查您的网络");
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
//    private static final int MSG_ADD_CAMERA_SUCCESS = 10;
//
//    private static final int MSG_ADD_CAMERA_FAIL = 12;
//
//    class MessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what) {
//                case MSG_GET_DEVICE_INFO_SUCCESS:
//                    LogUtils.e(TAG, "MSG_GET_DEVICE_INFO_SUCCESS-------");
////                    handleLocalValidateSerialNoFail(msg.arg1);
//
//                    break;
//                case MSG_GET_DEVICE_INFO_FAIL:
////                    handleLocalValidateCameraPswFail(msg.arg1);
//                    LogUtils.e(TAG, "MSG_GET_DEVICE_INFO_FAIL-------");
//                    break;
//                case MSG_QUERY_CAMERA_SUCCESS:
////                    handleQueryCameraSuccess();
//                    LogUtils.e(TAG, "MSG_QUERY_CAMERA_SUCCESS-------");
//                    ToastUtil.show(YsCameraNetSettingTipsActivity.this, "查询成功，该设备可增加");
//
////                    addQueryCameraAddVerifyCode(input_sn_cedt1.getText().toString().trim(),input_sn_cedt2.getText().toString().trim());
//                    break;
//                case MSG_QUERY_CAMERA_FAIL:
////                    handleQueryCameraFail(msg.arg1);
//                    LogUtils.e(TAG, "MSG_QUERY_CAMERA_FAIL-------");
////                    ToastUtil.show(YsCameraFoundActivity.this,"该设备不能新增");
////                    if(!StringUtils.isEmpty(mEZProbeDeviceInfo.getEZProbeDeviceInfo().getDefaultPicPath())){
////                        Glide.with(YsCameraFoundActivity.this).load(mEZProbeDeviceInfo.getEZProbeDeviceInfo().getDefaultPicPath()).error(R.drawable.results_pic_default).into(ys_found_camera_iv);
////                    }
//
//                    break;
//                case MSG_ADD_CAMERA_SUCCESS:
////                    handleAddCameraSuccess();
//                    LogUtils.e(TAG, "MSG_ADD_CAMERA_SUCCESS-------");
//                    ToastUtil.show(YsCameraNetSettingTipsActivity.this, "增加成功");
////                    SPUtils.put(YsCameraFoundActivity.this,"ysCamera"+input_sn_cedt1.getText().toString().trim(),input_sn_cedt2.getText().toString().trim());
//                    break;
//                case MSG_ADD_CAMERA_FAIL:
////                    handleAddCameraFail(msg.arg1);
//                    LogUtils.e(TAG, "MSG_ADD_CAMERA_FAIL-------");
//                    ToastUtil.show(YsCameraNetSettingTipsActivity.this, "增加失败");
//                    break;
//                case MSG_CAMERA_WIFI_CONFIG:
//                    mIntent.setClass(YsCameraNetSettingTipsActivity.this, YscameraNetSettingActivity.class);
////                    mIntent.putExtra("SerialNo", input_sn_cedt1.getText().toString().trim());
////                    mIntent.putExtra("very_code", input_sn_cedt2.getText().toString().trim());
//                    if (mEZProbeDeviceInfo != null && mEZProbeDeviceInfo.getEZProbeDeviceInfo() != null) {
//                        if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportWifi() == 3) {
//                            mIntent.putExtra("support_Wifi", true);
//                        }
//                        if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportAP() == 2) {
//                            mIntent.putExtra("support_Ap", true);
//                        }
//                        if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportSoundWave() == 1) {
//                            mIntent.putExtra("support_sound_wave", true);
//                        }
//                    } else {
//                        mIntent.putExtra("support_unknow_mode", true);
//                    }
////                    mIntent.putExtra("device_type", mDeviceType);
//                    startActivity(mIntent);
//                    break;
//                default:
//                    break;
//            }
//        }
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
//    protected static final int MSG_QUERY_CAMERA_FAIL = 0;
//    protected static final int MSG_QUERY_CAMERA_SUCCESS = 1;
//    protected static final int MSG_CAMERA_WIFI_CONFIG = 2;
//    protected static final int MSG_GET_DEVICE_INFO_SUCCESS = 3;
//    protected static final int MSG_GET_DEVICE_INFO_FAIL = 4;
//    private boolean needWifiConfig = false;
//
//    private EZProbeDeviceInfoResult mEZProbeDeviceInfo = null;
//
//    private void getYsCameraInfo(String devSerial, String devType) {
//
//        new Thread() {
//            public void run() {
//                mEZProbeDeviceInfo = EZOpenSDK.getInstance().probeDeviceInfo(devSerial, devType);//"137234674","CS-C2C-1A1WFR"
//                LogUtils.e("getYsCameraInfo", devSerial + "-----" + devType);
//                if (mEZProbeDeviceInfo.getBaseException() == null) {
//                    // TODO: 2018/6/25 添加设备
//                    sendMessage(MSG_QUERY_CAMERA_SUCCESS);
//                } else {
//                    switch (mEZProbeDeviceInfo.getBaseException().getErrorCode()) {
//
//                        case 120023:
//                            // TODO: 2018/6/25  设备不在线，未被用户添加 （这里需要网络配置）
//                            needWifiConfig = true;
//                            sendMessage(MSG_CAMERA_WIFI_CONFIG);
//                            break;
//                        case 120002:
//                            // TODO: 2018/6/25  设备不存在，未被用户添加 （这里需要网络配置）
//                            needWifiConfig = true;
//                            sendMessage(MSG_CAMERA_WIFI_CONFIG);
//                            break;
//                        case 120029:
//                            // TODO: 2018/6/25  设备不在线，已经被自己添加 (这里需要网络配置)
//                            needWifiConfig = true;
//                            LogUtil.infoLog(TAG, "probeDeviceInfo fail :" + mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            break;
//
//                        case 120020:
//                            // TODO: 2018/6/25 设备在线，已经被自己添加 (给出提示)
//                            sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            break;
//
//                        case 120022:
//                            // TODO: 2018/6/25  设备在线，已经被别的用户添加 (给出提示)
//                            sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            break;
//                        case 120024:
//                            // TODO: 2018/6/25  设备不在线，已经被别的用户添加 (给出提示)
//                            sendMessage(MSG_QUERY_CAMERA_FAIL, mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                            break;
//
//                        default:
//                            // TODO: 2018/6/25 请求异常
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showToast("Request failed = "
//                                            + mEZProbeDeviceInfo.getBaseException().getErrorCode());
//                                }
//                            });
//                            break;
//                    }
//                }
//
//            }
//        }.start();
//    }
//
//
//    private EZDeviceInfo result;
//
//    private void getDeviceInfo(String devSerial) {
//
//        // Local network detection
//        if (!ConnectionDetector.isNetworkAvailable(YsCameraNetSettingTipsActivity.this)) {
//            ToastUtil.show(YsCameraNetSettingTipsActivity.this, "请检查您的网络");
//            return;
//        }
//
//        new Thread() {
//            public void run() {
//
//                try {
//                    result = EZOpenSDK.getInstance().getDeviceInfo(devSerial);//"137234674", "KRCQDY"
//
//                    LogUtils.e(TAG, result + "----------------" + result.getDeviceCover());
//                    /***********If necessary, the developer needs to save this code***********/
//                    // 添加成功过后
//                    sendMessage(MSG_GET_DEVICE_INFO_SUCCESS);
//                } catch (BaseException e) {
//                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
//                    LogUtil.debugLog(TAG, errorInfo.toString());
//
//                    sendMessage(MSG_GET_DEVICE_INFO_FAIL, errorInfo.errorCode);
//                    LogUtil.errorLog(TAG, "get deviceInfo fail");
//                }
//
//            }
//        }.start();
//    }
//}
