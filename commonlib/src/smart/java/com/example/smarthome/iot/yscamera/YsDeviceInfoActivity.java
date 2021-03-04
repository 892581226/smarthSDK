//package com.example.smarthome.iot.yscamera;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.ClearEditText;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.SPUtils;
//import com.example.commonlib.base.uiutils.SpConstant;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.commonlib.base.uiutils.ToastUtil;
//import com.example.commonlib.base.uiutils.dialog.BaseDialog;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.adapter.DeviceAddressAdapter;
//import com.example.smarthome.iot.entry.CommonResp;
//import com.example.smarthome.iot.entry.DeviceInfoVo;
//import com.example.smarthome.iot.entry.SmartControlVo;
//import com.example.smarthome.iot.entry.SmartInfoVo;
//import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
//import com.example.smarthome.iot.net.Constant;
//import com.google.android.flexbox.AlignItems;
//import com.google.android.flexbox.FlexDirection;
//import com.google.android.flexbox.FlexWrap;
//import com.google.android.flexbox.FlexboxLayoutManager;
//import com.google.android.flexbox.JustifyContent;
//import com.lzy.okgo.OkGo;
//import com.lzy.okgo.convert.StringConvert;
//import com.lzy.okgo.model.Response;
//import com.lzy.okrx2.adapter.ObservableResponse;
//import com.videogo.errorlayer.ErrorInfo;
//import com.videogo.exception.BaseException;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.bean.EZDeviceInfo;
//import com.videogo.util.LogUtil;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.xhwl.commonlib.uiutils.ClearEditText;
//import com.xhwl.commonlib.uiutils.dialog.BaseDialog;
//import com.zhy.autolayout.AutoLinearLayout;
//import com.zyao89.view.zloading.ZLoadingDialog;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
//import static com.zyao89.view.zloading.Z_TYPE.ROTATE_CIRCLE;
//
///**
// * author:
// * date:
// * description: 萤石设备详情
// * update:
// * version:
// */
//public class YsDeviceInfoActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {
//
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private View mTitleLine;
//    /**
//     * 客厅
//     */
//    private TextView top_btn;
//    private ClearEditText ys_device_info_device_name;
//    /**
//     * 保存
//     */
//    private Button ys_device_address_save_btn;
//    private SmartInfoVo.FamilysBean.RoomsBean mRoomsBean;
//    private SmartInfoVo.FamilysBean.DeviceInfoBean mDeviceInfoBean;
//    private DeviceAddressAdapter mAddressAdapter;
//    private List<SmartInfoVo.FamilysBean.RoomsBean> mRoomsBeanList;
//
//    private String userId, familyId,devSerial;
//    private BaseDialog mBaseDialog;
//    private EZDeviceInfo mDeviceInfo = null;
//    private static final String TAG = YsDeviceInfoActivity.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_device_info);
//        initView();
//        initDate();
//    }
//
//    private void initDate() {
//        mTopTitle.setText("设备详情");
//        mTitleLine.setVisibility(View.GONE);
//
//        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
//
//        familyId = SPUtils.get(this, "familyId" + userId, "");
//
//        mDeviceInfo = getIntent().getParcelableExtra("ysDeviceInfoBean");
//        if(mDeviceInfo!=null){
//            ys_device_info_device_name.setText(mDeviceInfo.getDeviceName());
//            devSerial = mDeviceInfo.getDeviceSerial();
//        }
//        mMsgHandler = new MessageHandler();
//
//        mBaseDialog = new BaseDialog(this, 1)
//                .setTitleText("删除设备")
//                .setInfoText("设备删除后会清除与设备相关的所有数据，若再次使用需重新添加设备")
//                .setCancelVisity(true)
//                .setConfirmListener(this)
//                .setCancelListener(this);
//        mBaseDialog.setCancelable(false);
//
//        loadingDialog = new ZLoadingDialog(this);
//        loadingDialog.setLoadingBuilder(ROTATE_CIRCLE)//设置类型
//                .setLoadingColor(Color.BLUE)//颜色
//                .setHintText("Loading...")
//                .setHintTextSize(16) // 设置字体大小 dp
//                .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
//                .setCanceledOnTouchOutside(false);
//    }
//
//    private void initView() {
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        top_btn = findViewById(R.id.top_btn);
//        top_btn.setText("删除");
//        top_btn.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//        mTitleLine = (View) findViewById(R.id.title_line);
//
//        ys_device_info_device_name = (ClearEditText) findViewById(R.id.ys_device_info_device_name);
//        ys_device_address_save_btn = (Button) findViewById(R.id.ys_device_address_save_btn);
//        ys_device_address_save_btn.setOnClickListener(this);
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.top_back) {
//            finish();
//        } else if (v.getId() == R.id.top_btn) {
//            if (mDeviceInfo!=null) {
//                mBaseDialog.show();
//            }
//        } else if (v.getId() == R.id.ys_device_address_save_btn) {
//
//            if (!StringUtils.isEmpty(ys_device_info_device_name.getText().toString().trim())) {
//
////                    LogUtils.e("DeviceInfoActivity", mDeviceInfoBean.getRoomId() + "====" + mDeviceInfoBean.getRoomName());
////                    smartUpdateDevice(mDeviceInfoBean.getDeviceId(), mDeviceInfoDeviceName.getText().toString().trim(), mDeviceInfoBean.getRoomName(), mDeviceInfoBean.getRoomId());
////                    loadingDialog.show();
//                setYsDeviceName(ys_device_info_device_name.getText().toString().trim());
//            } else {
//                ToastUtil.show(YsDeviceInfoActivity.this, "设备名称为空");
//            }
//        }
//
//
//    }
//
//    private void setYsDeviceName(String name) {
//        new Thread() {
//            public void run() {
//
//                try {
//                    boolean result = EZOpenSDK.getInstance().setDeviceName(devSerial,name);//"137234674", "KRCQDY"
//
//                    LogUtils.e(TAG,result+"----------------");
//                    /***********If necessary, the developer needs to save this code***********/
//                    // 添加成功过后
//                    sendMessage(MSG_SET_DEVICE_NAME_SUCCESS);
//                } catch (BaseException e) {
//                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
//                    LogUtil.debugLog(TAG, errorInfo.toString());
//
//                    sendMessage(MSG_SET_DEVICE_NAME_FAIL, errorInfo.errorCode);
//                    LogUtil.errorLog(TAG, "setYsDeviceName fail");
//                }
//
//            }
//        }.start();
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            loadingDialog.dismiss();
//            if (msg.what == 200) {
//                ToastUtil.show(YsDeviceInfoActivity.this,"操作成功");
//                EventBus.getDefault().post(new UpdateFamilyEvent(true));
//                finish();
//                destoryActivity("SingleBitSwitchActivity");
//                destoryActivity("DoubleBitSwitchActivity");
//                destoryActivity("ThreeBitSwitchActivity");
//                destoryActivity("FourBitSwitchActivity");
//                destoryActivity("CurtainSwitchActivity");
//                destoryActivity("DoorMagnetActivity");
//                destoryActivity("HumanSensorActivity");
//                destoryActivity("HumitureSensorActivity");
//                destoryActivity("SceneSixSwitchActivity");
//            } else if (msg.what == 202) {
//                ToastUtil.show(YsDeviceInfoActivity.this,"操作失败");
//            }
//        }
//    };
//
//    @Override
//    public void onConfirmClick() {
//        mBaseDialog.dismiss();
//        deleteYsDevice();
//        loadingDialog.show();
//    }
//
//    private void deleteYsDevice() {
//        //删除摄像头
//        if(!StringUtils.isEmpty(devSerial)){
//            OkGo.<String>get(Constant.HOST+Constant.Ezviz_DelDev)
//                    .params("deviceSerial", devSerial)
//                    .params("userId", userId)
//                    .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
//                    .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
//                    .subscribeOn(Schedulers.io())
//                    .doOnSubscribe(new Consumer<Disposable>() {
//                        @Override
//                        public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
////                        showLoading();
//                            LogUtils.e("accept",disposable.isDisposed()+"===");
//                        }
//                    })
//                    .map(new Function<Response<String>, CommonResp>() {
//                        @Override
//                        public CommonResp apply(Response<String> stringResponse) throws Exception {
//                            LogUtils.e("apply","===="+stringResponse.body());
//                            CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
//                            LogUtils.e("resp",resp.getState()+"=====");
//                            return resp;
//                        }
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())//
//                    .subscribe(new Observer<CommonResp>() {
//                        @Override
//                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
////                        addDisposable(d);
//                            LogUtils.e("onSubscribe","========---====");
//                        }
//
//                        @Override
//                        public void onNext(CommonResp resp) {
//                            if(resp !=null){
//                                if(resp.getErrorCode().equalsIgnoreCase("200")){
//                                    sendMessage(EZVIZ_DELETE_DEVICE_SUCCESS);
//                                } else {
//                                    sendMessage(EZVIZ_DELETE_DEVICE_FAIL);
//                                }
//                            } else {
//                                sendMessage(EZVIZ_DELETE_DEVICE_FAIL);
//                            }
//
//                        }
//
//
//                        @Override
//                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                            e.printStackTrace();
//                            sendMessage(EZVIZ_DELETE_DEVICE_FAIL);
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            loadingDialog.dismiss();
//                            LogUtils.e("onComplete","============");
//                        }
//                    });
//        }
//    }
//
//    @Override
//    public void onCancelListener() {
//        if (mBaseDialog.isShowing()) {
//            mBaseDialog.dismiss();
//        }
//    }
//
//    class MessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_SET_DEVICE_NAME_SUCCESS:
//                    LogUtils.e(TAG,"MSG_SET_DEVICE_NAME_SUCCESS-------");
//                    ToastUtil.show(YsDeviceInfoActivity.this,"修改名称成功");
//                    EventBus.getDefault().post("updateCameraName:"+ys_device_info_device_name.getText().toString().trim());
//                    finish();
//                    break;
//                case MSG_SET_DEVICE_NAME_FAIL:
//////                    handleLocalValidateCameraPswFail(msg.arg1);
//                    LogUtils.e(TAG,"MSG_SET_DEVICE_NAME_FAIL-------");
//                    ToastUtil.show(YsDeviceInfoActivity.this,"修改名称失败");
//                    break;
//                case EZVIZ_DELETE_DEVICE_SUCCESS:
//                    ToastUtil.show(YsDeviceInfoActivity.this,"删除成功");
//                    finish();
//                    destoryActivity("YsCameraActivity");
//                    EventBus.getDefault().post("YsCameraRefresh");
//                    break;
//                case EZVIZ_DELETE_DEVICE_FAIL:
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    private static final int MSG_SET_DEVICE_NAME_SUCCESS = 13;
//
//    private static final int MSG_SET_DEVICE_NAME_FAIL = 14;
//    private static final int EZVIZ_DELETE_DEVICE_SUCCESS = 15;
//
//    private static final int EZVIZ_DELETE_DEVICE_FAIL = 16;
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
//}