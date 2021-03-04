//package com.example.smarthome.iot.yscamera;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
////import com.example.commonlib.base.BaseActivity;
////import com.example.commonlib.base.uiutils.ClearEditText;
////import com.example.commonlib.base.uiutils.LogUtils;
////import com.example.commonlib.base.uiutils.SPUtils;
////import com.example.commonlib.base.uiutils.SpConstant;
////import com.example.commonlib.base.uiutils.StringUtils;
////import com.example.commonlib.base.uiutils.ToastUtil;
////import com.example.commonlib.base.uiutils.dialog.BaseDialog;
////import com.example.commonlib.base.uiutils.dialog.BaseEditDialog;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.entry.CommonResp;
//import com.example.smarthome.iot.entry.ShareGetUserId;
//import com.example.smarthome.iot.entry.SmartInfoVo;
//import com.example.smarthome.iot.entry.SubUserListVo;
//import com.example.smarthome.iot.net.Constant;
//import com.lidroid.xutils.exception.BaseException;
//import com.lzy.okgo.OkGo;
//import com.lzy.okgo.convert.StringConvert;
//import com.lzy.okgo.model.Response;
//import com.lzy.okrx2.adapter.ObservableResponse;
////import com.videogo.errorlayer.ErrorInfo;
////import com.videogo.exception.BaseException;
////import com.videogo.openapi.EZOpenSDK;
////import com.videogo.openapi.bean.EZProbeDeviceInfoResult;
////import com.videogo.util.ConnectionDetector;
////import com.videogo.util.LocalValidate;
////import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.xhwl.commonlib.uiutils.ClearEditText;
//import com.xhwl.commonlib.uiutils.LogUtils;
//import com.xhwl.commonlib.uiutils.SPUtils;
//import com.xhwl.commonlib.uiutils.StringUtils;
//import com.xhwl.commonlib.uiutils.ToastUtil;
//import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
//import com.xhwl.commonlib.uiutils.dialog.BaseEditDialog;
//import com.zhy.autolayout.AutoLinearLayout;
//import com.zyao89.view.zloading.ZLoadingDialog;
//
//import java.util.ArrayList;
//import java.util.List;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
//import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;
//import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;
//
///**
// *
// */
//public class ManualSnActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {
//
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private TextView mTopBtn;
//    private ImageView mTopRecord;
//
//    private ClearEditText input_sn_cedt1,input_sn_cedt2;
//    private String deviceType;
//
//
//    private boolean isManageMode;
//    private Intent mIntent = new Intent();
//    private BaseDialog mRemoveSubuserBaseDialog;
//    private BaseEditDialog mBaseEditDialog;
//    private String ids;
//    public List<SubUserListVo.SubUserListBean> mUserList = new ArrayList<>();
//    private RelativeLayout input_sn_search_rl;
//    private ImageView share_phone_select;
//    private TextView share_phone_result_tv;
//    private boolean resultSelected = false;
//    private static final String TAG = "ManualSnActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_iot_input_sn);
//        addDestoryActivity(this,TAG);
//        initView();
//        initDate();
//    }
//
//    private void initDate() {
//        mTopTitle.setText("输入序列号");
//        Bundle bundle = getIntent().getExtras();
//        if(bundle!=null){
//            input_sn_cedt1.setText(bundle.getString("SerialNo"));
//            input_sn_cedt2.setText(bundle.getString("very_code"));
//            deviceType = bundle.getString("DeviceType");
//        }
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
//        mMsgHandler = new MessageHandler();
//
//        input_sn_search_rl = findViewById(R.id.input_sn_search_rl);
//        input_sn_search_rl.setOnClickListener(this);
//        input_sn_cedt1 = (ClearEditText) findViewById(R.id.input_sn_cedt1);
//        input_sn_cedt2 = (ClearEditText) findViewById(R.id.input_sn_cedt2);
//
////        input_sn_cedt1.addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////
////            }
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
//////                share_phone_cedt.onTextChanged(s,start,before,count);
////            }
////
////            @Override
////            public void afterTextChanged(Editable s) {
////
////            }
////        });
//
//        loadingDialog = new ZLoadingDialog(this);
//        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
//                .setLoadingColor(Color.BLUE)//颜色
//                .setHintText("Loading...")
//                .setHintTextSize(16) // 设置字体大小 dp
//                .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
//                .setCanceledOnTouchOutside(false);
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
//        } else if (i == R.id.input_sn_search_rl) {//
//            if(!StringUtils.isEmpty(input_sn_cedt1.getText().toString().trim()) && !StringUtils.isEmpty(input_sn_cedt2.getText().toString().trim())){
////                getYsCameraInfo(input_sn_cedt1.getText().toString().trim(),"");//deviceType
//                isValidate(input_sn_cedt1.getText().toString().trim());
//            } else {
//                ToastUtil.show(ManualSnActivity.this,"设备序列号或验证码为空");
//            }
//
//        }
//    }
//
//
//
//    @Override
//    public void onConfirmClick() {
//
//    }
//
//    @Override
//    public void onCancelListener() {
//        // 执行了dialog 取消操作
//        if (mRemoveSubuserBaseDialog.isShowing()) {
//            mRemoveSubuserBaseDialog.dismiss();
//        }
//    }
//
//    private void addQueryCameraAddVerifyCode(String devSerial,String verCode) {
//
//        // Local network detection
//        if (!ConnectionDetector.isNetworkAvailable(ManualSnActivity.this)) {
//            ToastUtil.show(ManualSnActivity.this,"添加失败，请检查您的网络");
//            return;
//        }
//
//        new Thread() {
//            public void run() {
//
//                try {
//                    boolean result = EZOpenSDK.getInstance().addDevice(devSerial,verCode);//"137234674", "KRCQDY"
//
//                    LogUtils.e(TAG,result+"----------------");
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
//            switch (msg.what) {
////                case MSG_LOCAL_VALIDATE_SERIALNO_FAIL:
////                    LogUtils.e(TAG,"MSG_LOCAL_VALIDATE_SERIALNO_FAIL-------");
//////                    handleLocalValidateSerialNoFail(msg.arg1);
////                    break;
////                case MSG_LOCAL_VALIDATE_CAMERA_PSW_FAIL:
//////                    handleLocalValidateCameraPswFail(msg.arg1);
////                    LogUtils.e(TAG,"MSG_LOCAL_VALIDATE_CAMERA_PSW_FAIL-------");
////                    break;
//                case MSG_QUERY_CAMERA_SUCCESS:
////                    handleQueryCameraSuccess();
//                    LogUtils.e(TAG,"MSG_QUERY_CAMERA_SUCCESS-------");
//                    ToastUtil.show(ManualSnActivity.this,"查询成功，该设备可增加");
//                    addQueryCameraAddVerifyCode(input_sn_cedt1.getText().toString().trim(),input_sn_cedt2.getText().toString().trim());
//                    break;
//                case MSG_QUERY_CAMERA_FAIL:
////                    handleQueryCameraFail(msg.arg1);
//                    LogUtils.e(TAG,"MSG_QUERY_CAMERA_FAIL-------");
//                    ToastUtil.show(ManualSnActivity.this,"该设备不能新增");
//                    break;
//                case MSG_ADD_CAMERA_SUCCESS:
////                    handleAddCameraSuccess();
//                    LogUtils.e(TAG,"MSG_ADD_CAMERA_SUCCESS-------");
//                    ToastUtil.show(ManualSnActivity.this,"增加成功");
//                    SPUtils.put(ManualSnActivity.this,"ysCamera"+input_sn_cedt1.getText().toString().trim(),input_sn_cedt2.getText().toString().trim());
//                    break;
//                case MSG_ADD_CAMERA_FAIL:
////                    handleAddCameraFail(msg.arg1);
//                    LogUtils.e(TAG,"MSG_ADD_CAMERA_FAIL-------");
//                    ToastUtil.show(ManualSnActivity.this,"增加失败");
//                    break;
//                case MSG_CAMERA_WIFI_CONFIG:
//                    mIntent.setClass(ManualSnActivity.this,YscameraNetSettingActivity.class);
//                    mIntent.putExtra("SerialNo", input_sn_cedt1.getText().toString().trim());
//                    mIntent.putExtra("very_code", input_sn_cedt2.getText().toString().trim());
//                    if (mEZProbeDeviceInfo != null && mEZProbeDeviceInfo.getEZProbeDeviceInfo() != null){
//                        if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportWifi() == 3){
//                            mIntent.putExtra("support_Wifi", true);
//                        }
//                        if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportAP() == 2){
//                            mIntent.putExtra("support_Ap", true);
//                        }
//                        if (mEZProbeDeviceInfo.getEZProbeDeviceInfo().getSupportSoundWave() == 1){
//                            mIntent.putExtra("support_sound_wave", true);
//                        }
//                    }else{
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
//    private EZProbeDeviceInfoResult mEZProbeDeviceInfo = null;
//    private void getYsCameraInfo(String devSerial,String devType) {
//
//        new Thread() {
//            public void run() {
//                mEZProbeDeviceInfo = EZOpenSDK.getInstance().probeDeviceInfo(devSerial,devType);//"137234674","CS-C2C-1A1WFR"
//                LogUtils.e("getYsCameraInfo",devSerial+"-----"+devType);
//                if (mEZProbeDeviceInfo.getBaseException() == null){
//                    // TODO: 2018/6/25 添加设备
//                    sendMessage(MSG_QUERY_CAMERA_SUCCESS);
//                }else{
//                    switch (mEZProbeDeviceInfo.getBaseException().getErrorCode()){
//
//                        case 120023:
//                            // TODO: 2018/6/25  设备不在线，未被用户添加 （这里需要网络配置）
//                            sendMessage(MSG_CAMERA_WIFI_CONFIG);
//                            break;
//                        case 120002:
//                            // TODO: 2018/6/25  设备不存在，未被用户添加 （这里需要网络配置）
//                            sendMessage(MSG_CAMERA_WIFI_CONFIG);
//                            break;
//                        case 120029:
//                            // TODO: 2018/6/25  设备不在线，已经被自己添加 (这里需要网络配置)
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
//    private LocalValidate mLocalValidate = null;
//    private void isValidate(String mSerialNoStr) {
//        mLocalValidate = new LocalValidate();
//        try {
//            mLocalValidate.localValidatSerialNo(mSerialNoStr);
//            LogUtil.infoLog(TAG, mSerialNoStr);
//        } catch (BaseException e) {
////            handleLocalValidateSerialNoFail(e.getErrorCode());
//            ToastUtil.show(ManualSnActivity.this,"序列号错误");
//            LogUtil.errorLog(TAG, "searchCameraBySN-> local validate serial no fail, errCode:" + e.getErrorCode());
//            return;
//        }
//
//        Bundle bundle = new Bundle();
//        bundle.putString("SerialNo", input_sn_cedt1.getText().toString().trim());
//        bundle.putString("very_code", input_sn_cedt2.getText().toString().trim());
//        bundle.putString("DeviceType", "");
//
//        Intent intent = new Intent(ManualSnActivity.this, YsCameraFoundActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//}
