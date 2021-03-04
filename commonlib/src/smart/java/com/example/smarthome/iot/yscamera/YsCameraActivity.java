//package com.example.smarthome.iot.yscamera;
//
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.DisplayMetrics;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.SPUtils;
//import com.example.commonlib.base.uiutils.SpConstant;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.commonlib.base.uiutils.ToastUtil;
//import com.example.smarthome.R;
//
//import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
//import com.videogo.constant.Constant;
//import com.videogo.constant.IntentConsts;
//import com.videogo.errorlayer.ErrorInfo;
//import com.videogo.exception.BaseException;
//import com.videogo.exception.ErrorCode;
//import com.videogo.openapi.EZConstants;
//import com.videogo.openapi.EZOpenSDK;
//import com.videogo.openapi.EZPlayer;
//import com.videogo.openapi.bean.EZCameraInfo;
//import com.videogo.openapi.bean.EZDeviceInfo;
//import com.videogo.realplay.RealPlayStatus;
//import com.videogo.util.LogUtil;
//import com.videogo.util.Utils;
//import com.videogo.widget.CustomRect;
//import com.videogo.widget.CustomTouchListener;
//import com.xhwl.commonlib.base.BaseActivity;
//import com.zhy.autolayout.AutoLinearLayout;
//import com.videogo.util.ConnectionDetector;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//
///**
// * author:
// * date:
// * description:
// * update:
// * version:
// */
//public class YsCameraActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback,VerifyCodeInput.VerifyCodeInputListener {
//
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private TextView mTopBtn;
//    private View mTitleLine;
//
//    private SurfaceView mRealPlaySv = null;
//    private SurfaceHolder mRealPlaySh = null;
//    private EZPlayer player;
//    private EZDeviceInfo mDeviceInfo = null;
//    private EZCameraInfo mCameraInfo = null;
//    private String fyCode,fromTag,devSerial,userId;
//    private static final String TAG = YsCameraActivity.class.getSimpleName();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//        setContentView(R.layout.activity_ys_camera);
//        addDestoryActivity(this, TAG);
//        initView();
//        initDate();
//    }
//
//    private void initDate() {
//        mTitleLine.setVisibility(View.GONE);
//        mTopBtn.setText("管理");
//        userId = SPUtils.get(YsCameraActivity.this, SpConstant.SP_USER_TELEPHONE,"");
//
//        fromTag = getIntent().getStringExtra("fromTag");
//        if(!StringUtils.isEmpty(fromTag) && fromTag.equalsIgnoreCase("YsAddDevicePercentActivity")){
//            mMsgHandler = new MessageHandler();
//            devSerial = getIntent().getStringExtra("devSerial");
//            fyCode = getIntent().getStringExtra("very_code");
//            getDeviceInfo(devSerial);
//        } else {
//            mDeviceInfo = getIntent().getParcelableExtra(IntentConsts.EXTRA_DEVICE_INFO);
//            mCameraInfo = EZUtils.getCameraInfoFromDevice(mDeviceInfo, 0);
//            startRealPlay();
//        }
//
//        //设置播放参数
////        mPlayer.setUrl("ezopen://KRCQDY@open.ys7.com/137234674/1.hd.live");
//        //创建loadingview
////        ProgressBar mLoadView = new ProgressBar(this);
////        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
////                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
////        mLoadView.setLayoutParams(lp);
////        //设置loadingview
////        mPlayer.setLoadingView(mLoadView);
//    }
//
//    private void initView() {
//        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
//        mTopBack.setOnClickListener(this);
//        mTopTitle = (TextView) findViewById(R.id.top_title);
//        mTopBtn = (TextView) findViewById(R.id.top_btn);
//        mTopBtn.setOnClickListener(this);
//        mTitleLine = (View) findViewById(R.id.title_line);
//
//        mTopTitle.setText("萤石");
//
//        mRealPlaySv =  findViewById(R.id.realplay_sv);
//        mRealPlaySh = mRealPlaySv.getHolder();
//        mRealPlaySh.addCallback(YsCameraActivity.this);
//
//        mRealPlayTouchListener = new CustomTouchListener() {
//            @Override
//            public boolean canZoom(float scale) {
////                if (mStatus == RealPlayStatus.STATUS_PLAY) {
//                    return true;
////                } else {
////                    return false;
////                }
//            }
//
//            @Override
//            public boolean canDrag(int direction) {
////                if (mStatus != RealPlayStatus.STATUS_PLAY) {
////                    return false;
////                }
////                if (mEZPlayer != null && mDeviceInfo != null) {
////                    // 出界判断 Out of bounds
////                    if (DRAG_LEFT == direction || DRAG_RIGHT == direction) {
////                        // 左移/右移出界判断 Left / right out of bounds
////                        if (mDeviceInfo.isSupportPTZ()) {
////                            return true;
////                        }
////                    } else if (DRAG_UP == direction || DRAG_DOWN == direction) {
////                        // 上移/下移出界判断  Move up / down to judge
////                        if (mDeviceInfo.isSupportPTZ()) {
////                            return true;
////                        }
////                    }
////                }
//                return false;
//            }
//
//            @Override
//            public void onSingleClick() {
////                onRealPlaySvClick();
//            }
//
//            @Override
//            public void onDoubleClick(MotionEvent e) {
//            }
//
//            @Override
//            public void onZoom(float scale) {
////                LogUtil.debugLog(TAG, "onZoom:" + scale);
//                if (player != null && mDeviceInfo != null &&  mDeviceInfo.isSupportZoom()) {
//                    startZoom(scale);
//                }
//            }
//
//            @Override
//            public void onDrag(int direction, float distance, float rate) {
////                LogUtil.debugLog(TAG, "onDrag:" + direction);
////                if (mEZPlayer != null) {
////                    startDrag(direction, distance, rate);
////                }
//            }
//
//            @Override
//            public void onEnd(int mode) {
////                LogUtil.debugLog(TAG, "onEnd:" + mode);
//                if (player != null) {
////                    stopDrag(false);
//                }
//                if (player != null && mDeviceInfo != null && mDeviceInfo.isSupportZoom()) {
////                    stopZoom();
//                }
//            }
//
//            @Override
//            public void onZoomChange(float scale, CustomRect oRect, CustomRect curRect) {
////                LogUtil.debugLog(TAG, "onZoomChange:" + scale);
//                if (player != null && mDeviceInfo != null && mDeviceInfo.isSupportZoom()) {
//                    //采用云台调焦 Using PTZ focus
//                    return;
//                }
////                if (mStatus == RealPlayStatus.STATUS_PLAY) {
//                    if (scale > 1.0f && scale < 1.1f) {
//                        scale = 1.1f;
//                    }
//                    setPlayScaleUI(scale, oRect, curRect);
////                }
//            }
//        };
//        mRealPlaySv.setOnTouchListener(mRealPlayTouchListener);
//
//    }
//
//    // 播放比例 Play ratio
//    private float mPlayScale = 1;
//    private void setPlayScaleUI(float scale, CustomRect oRect, CustomRect curRect) {
//        if (scale == 1) {
//
//            try {
//                if (player != null) {
//                    player.setDisplayRegion(false, null, null);
//                }
//            } catch (BaseException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } else {
//
//            if (mPlayScale == scale) {
//                try {
//                    if (player != null) {
//                        player.setDisplayRegion(true, oRect, curRect);
//                    }
//                } catch (BaseException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                return;
//            }
//            try {
//                if (player != null) {
//                    player.setDisplayRegion(true, oRect, curRect);
//                }
//            } catch (BaseException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        mPlayScale = scale;
//    }
//
//
//    private float mZoomScale = 0;
//    private void startZoom(float scale) {
//        if (player == null) {
//            return;
//        }
//
////        hideControlRlAndFullOperateBar(false);
//        boolean preZoomIn = mZoomScale > 1.01 ? true : false;
//        boolean zoomIn = scale > 1.01 ? true : false;
//        if (mZoomScale != 0 && preZoomIn != zoomIn) {
//            LogUtils.e("startZoom", "startZoom stop:" + mZoomScale);
//            //            mEZOpenSDK.controlPTZ(mZoomScale > 1.01 ? RealPlayStatus.PTZ_ZOOMIN
//            //                    : RealPlayStatus.PTZ_ZOOMOUT, RealPlayStatus.PTZ_SPEED_DEFAULT, EZPlayer.PTZ_COMMAND_STOP);
//            mZoomScale = 0;
//        }
//        if (scale != 0 && (mZoomScale == 0 || preZoomIn != zoomIn)) {
//            mZoomScale = scale;
//            LogUtils.e("startZoom", "startZoom start:" + mZoomScale);
//            //            mEZOpenSDK.controlPTZ(mZoomScale > 1.01 ? RealPlayStatus.PTZ_ZOOMIN
//            //                    : RealPlayStatus.PTZ_ZOOMOUT, RealPlayStatus.PTZ_SPEED_DEFAULT, EZPlayer.PTZ_COMMAND_START);
//        }
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        int viewId = v.getId();
//        if(viewId == R.id.top_back){
//            finish();
//        } else if(viewId == R.id.top_btn){
//            // 管理
//                Intent intent = new Intent(YsCameraActivity.this, YsDeviceInfoActivity.class);
//                intent.putExtra("ysDeviceInfoBean",mDeviceInfo);
//                startActivity(intent);
//        }
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        //停止播放
////        player.stopRealPlay();
//        stopRealPlay();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //释放资源
//        player.release();
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        player.resumePlayback();
//        if(player!=null){
//            player.startRealPlay();
//        }
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        if (player != null) {
//            //设置播放器的显示Surface
//            player.setSurfaceHold(holder);
//        } else {
//
//        }
//        mRealPlaySh = holder;
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (player != null) {
//            player.setSurfaceHold(null);
//        }
//        mRealPlaySh = null;
//    }
//
//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            LogUtils.e("handler", "handleMessage:" + msg.what);
//            switch (msg.what){
//                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
//                    if(!StringUtils.isEmpty(fyCode)){
//                        SPUtils.put(YsCameraActivity.this,"very_code"+mCameraInfo.getDeviceSerial()+userId,fyCode);
//                    }
//                    setRealPlaySvLayout();
//                    break;
//                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
////                    stopRealPlay();
//                    int errorCode = 0;
//                    Object obj = msg.obj;
//                    if (obj != null) {
//                        ErrorInfo errorInfo = (ErrorInfo) obj;
//                        errorCode = errorInfo.errorCode;
//                        LogUtils.e("MSG_REALPLAY_PLAY_FAIL", "handlePlayFail:" + errorInfo.errorCode);
//                        // 收到这两个错误码，可以弹出对话框，让用户输入密码后，重新取流预览
//                        if(errorCode == ErrorCode.ERROR_INNER_VERIFYCODE_NEED||errorCode == ErrorCode.ERROR_INNER_VERIFYCODE_ERROR){
//                            VerifyCodeInput.VerifyCodeInputDialog(YsCameraActivity.this, YsCameraActivity.this).show();
//                        }
//
//                    }
//                    break;
//            }
//        }
//    };
//
//    private void startRealPlay() {
//        if(mCameraInfo!=null){
//            player  = EZOpenSDK.getInstance().createPlayer(mCameraInfo.getDeviceSerial(),mCameraInfo.getCameraNo());
//            //设置Handler, 该handler将被用于从播放器向handler传递消息
//            player.setHandler(handler);
//
//            String verifyCode = SPUtils.get(YsCameraActivity.this,"very_code"+mCameraInfo.getDeviceSerial()+userId,"");
//            LogUtils.e(TAG,verifyCode+"----");
//            if(mDeviceInfo.getIsEncrypt() ==1 && !StringUtils.isEmpty(verifyCode)){//加密
//                player.setPlayVerifyCode(verifyCode);
//            }
//            //设置播放器的显示Surface
//
////            String verifyCode = SPUtils.getString(YsCameraActivity.this,"ysCamera"+mCameraInfo.getDeviceSerial());
//            /**
//             * 设备加密的需要传入密码
//             * 传入视频加密密码，用于加密视频的解码，该接口可以在收到ERROR_INNER_VERIFYCODE_NEED或ERROR_INNER_VERIFYCODE_ERROR错误回调时调用
//             * @param verifyCode 视频加密密码，默认为设备的6位验证码
//             */
////            player.setPlayVerifyCode(verifyCode);
//        }
//
//
//        //开启直播
////        player.startRealPlay();
//
//    }
//
//    private void stopRealPlay() {
//        LogUtils.e("YsCameraActivity", "stopRealPlay");
//
//        if (player != null) {
//            player.stopRealPlay();
//        }
//    }
//
//    private CustomTouchListener mRealPlayTouchListener = null;
//    private void setRealPlaySvLayout() {
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int whdth = dm.widthPixels;
//        int height = dm.heightPixels;
//        mRealPlayTouchListener.setSacaleRect(Constant.MAX_SCALE, 0, 0, whdth, height);
//        setPlayScaleUI(1, null, null);
//    }
//
//    @Override
//    public void onInputVerifyCode(String verifyCode) {
//        LogUtils.e("YsCameraActivity", "verify code is " + verifyCode);
//        fyCode = verifyCode;
//        if (player != null) {
//            startRealPlay(fyCode);
//        }
//    }
//
//    private void startRealPlay(String vcode) {
//        if(mCameraInfo!=null){
//            player  = EZOpenSDK.getInstance().createPlayer(mCameraInfo.getDeviceSerial(),mCameraInfo.getCameraNo());
//            /**
//             * 设备加密的需要传入密码
//             * 传入视频加密密码，用于加密视频的解码，该接口可以在收到ERROR_INNER_VERIFYCODE_NEED或ERROR_INNER_VERIFYCODE_ERROR错误回调时调用
//             * @param verifyCode 视频加密密码，默认为设备的6位验证码
//             */
//            player.setPlayVerifyCode(vcode);
//            player.setSurfaceHold(mRealPlaySh);
//            //设置Handler, 该handler将被用于从播放器向handler传递消息
//            player.setHandler(handler);
//            player.startRealPlay();
//        }
//
//    }
//
//
//    private EZDeviceInfo result;
//
//    private void getDeviceInfo(String devSerial) {
//
//        // Local network detection
//        if (!ConnectionDetector.isNetworkAvailable(YsCameraActivity.this)) {
//            ToastUtil.show(YsCameraActivity.this, "请检查您的网络");
//            return;
//        }
//        new Thread() {
//            public void run() {
//
//                try {
//                    result = EZOpenSDK.getInstance().getDeviceInfo(devSerial);//"137234674", "KRCQDY"
//
//                    LogUtils.e(TAG, result + "----------------" + result.getDeviceCover());
//                    /***********If necessary, the developer needs to save this code***********/
//                    // 成功过后
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
//    protected static final int MSG_GET_DEVICE_INFO_SUCCESS = 3;
//    protected static final int MSG_GET_DEVICE_INFO_FAIL = 4;
//    class MessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_GET_DEVICE_INFO_SUCCESS:
//                    LogUtils.e(TAG, "MSG_GET_DEVICE_INFO_SUCCESS-------");
////                    handleLocalValidateSerialNoFail(msg.arg1);
//                    if (result != null) {
//                        LogUtils.e(TAG, result.getDeviceCover() + "-------");
//                        mDeviceInfo = result;
//                        mCameraInfo = EZUtils.getCameraInfoFromDevice(mDeviceInfo, 0);
//
//                        startRealPlay(fyCode);
//                    }
//                    break;
//                case MSG_GET_DEVICE_INFO_FAIL:
////                    handleLocalValidateCameraPswFail(msg.arg1);
//                    LogUtils.e(TAG, "MSG_GET_DEVICE_INFO_FAIL-------");
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void Event(String updateCameraName) {
//        LogUtils.e(TAG,updateCameraName);
//        if (updateCameraName.contains("updateCameraName")) {
//            //
//            LogUtils.e(TAG,updateCameraName.substring(updateCameraName.indexOf(":")+1));
//            mDeviceInfo.setDeviceName(updateCameraName.substring(updateCameraName.indexOf(":")+1));
//        }
//    }
//}
