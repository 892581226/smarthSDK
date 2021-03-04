package com.example.smarthome.iot.impl.device.cateye.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.ELog;
import com.eques.icvss.utils.Method;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.cateye.entity.DevDetailsEntity;
import com.example.smarthome.iot.impl.device.cateye.entity.EventBusEntity;
import com.example.smarthome.iot.impl.device.cateye.utils.AvcEncoder;
import com.example.smarthome.iot.impl.device.cateye.utils.Constants;
import com.example.smarthome.iot.impl.device.cateye.utils.FileHelper;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.xhwl.commonlib.base.BaseActivity;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ArrayBlockingQueue;

public class VideoCallActivity extends BaseActivity implements Camera.PreviewCallback {
    private final String TAG = "VideoCallActivity";

    private SurfaceView phoneSurfaceView;
    private SurfaceView devSurfaceView;
    private RelativeLayout rlVideoCall;
    private Button btnCapture, btnMute, btnHangupCall, btnSoundSwitch;
    private Button btnVoiReply, btnChangeSound;
    private Button btnChangeDevCamera, btnSwitchPhoneCamera;
    private LinearLayout llCameraParent;


    private String callId;
    // 当前音量值
    private int currVolumeValue;
    // 设备类型
    private int devType = 0;
    // 默认音量值，用于关闭当前恢复默认数值
    private int defVolumeValue;
    // 是否是静音（禁止声音外放）
    private boolean isMuteFlag;
    private AudioManager audioManager;
    private SurfaceHolder telSurfaceHolder;
    /**
     * 设备详情数据
     */
    private DevDetailsEntity detailsEntity;
    /**
     * 抓拍尺寸
     */
    private int snapSizeW = 960;
    private int snapSizeH = 1280;

    /**
     * 屏幕宽高
     */
    private int screenWidthDip;
    private int screenHeightDip;
    private String uid;

    /**
     * 是否视频接听
     */
    private boolean hasVideo;
    /**
     * 门铃呼叫类型（1：门内 0：门外）
     */
    private int callType = 0;
    /**
     * 是否支持双向语音（H5、S1_Pro、S2）
     */
    private boolean isSupportDoubleTalk = true;
    /**
     * 是否支持双向视频（S1_Pro、S2）
     */
    private boolean isSupportDoubleVideo = false;
    /**
     * 是否开启双向对讲
     */
    private boolean isDoubleTalk = false;
    /**
     * 手机摄像头开启与否标识
     */
    private boolean openPhoneCamera = true;

    /**
     * 摄像头相关
     */
    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    // 待解码视频缓冲队列，静态成员！
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<>(10);

    private ICVSSUserInstance icvss;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.videocall_capture_activity);

        EventBus.getDefault().register(VideoCallActivity.this);

        icvss = ICVSSUserModule.getInstance(null).getIcvss();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        defVolumeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, defVolumeValue, 0);

        currVolumeValue = defVolumeValue;

        getIntentData();
        // 获取设备详情数据
//        getDevDetailsData();

//        icvss.equesWakeUp(uid);

        initUI();

        if (!isSupportDoubleTalk) {
            boolean bo = audioManager.isWiredHeadsetOn();
            if (!bo) {
                openSpeaker();
            }
        }
//        handler.sendEmptyMessageDelayed(110,5000);
    }

//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 110:
//                    icvss.equesWakeUp(uid);
//                    handler.sendEmptyMessageDelayed(110,5000);
//                    break;
//            }
//        }
//    };

    /**
     * 获取数据
     */
    public void getIntentData() {
        devType = getIntent().getIntExtra(Method.ATTR_ROLE, BuddyType.TYPE_WIFI_DOOR_R22);
        callType = getIntent().getIntExtra(Method.METHOD_ATTR_CAMERATYPE, 0);
        uid = getIntent().getStringExtra(Method.ATTR_BUDDY_UID);
        hasVideo = getIntent().getBooleanExtra(Method.ATTR_CALL_HASVIDEO, false);
//        detailsEntity = (DevDetailsEntity) getIntent().getSerializableExtra("devDetailsEntity");
        ELog.e(TAG, " devType: ", devType, " callType: ", callType, " hasVideo: ", !hasVideo);
    }

    private void initUI() {
        devSurfaceView = findViewById(R.id.dev_surface_view);
        phoneSurfaceView = findViewById(R.id.phone_surface_view);
        llCameraParent = findViewById(R.id.ll_camera_parent);

        btnVoiReply = findViewById(R.id.btn_voi_reply);
        btnVoiReply.setOnClickListener(new MyOnClickListener());

        btnChangeSound = findViewById(R.id.btn_change_sound);
        btnChangeSound.setOnClickListener(new MyOnClickListener());

        btnCapture = findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(new MyOnClickListener());

        btnMute = findViewById(R.id.btn_mute);
        btnMute.setOnClickListener(new MyOnClickListener());

        btnHangupCall = findViewById(R.id.btn_hangupCall);
        btnHangupCall.setOnClickListener(new MyOnClickListener());

        btnChangeDevCamera = findViewById(R.id.btn_change_dev_camera);
        btnChangeDevCamera.setOnClickListener(new MyOnClickListener());

        btnSwitchPhoneCamera = findViewById(R.id.btn_switch_phone_camera);
        btnSwitchPhoneCamera.setOnClickListener(new MyOnClickListener());

        rlVideoCall = findViewById(R.id.rl_video_call);
        rlVideoCall.setOnClickListener(new MyOnClickListener());

        btnSoundSwitch = findViewById(R.id.btn_soundSwitch);

        setVideoSize();
        setAnsweringWayUI();
        setSurfVWindow();

        callOpen();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(EventBusEntity busEntity) {
        Log.e("事件总线返回的数据",busEntity.toString());
        int actionCode = busEntity.getActionCode();
        switch (actionCode) {
            case Constants.EVENTBUS_DISPATCH_CHANNEL_SETUP_SUCCESSFUL:
                // 视频画面接收，处理操作
                if (!hasVideo) {
                    ELog.showToastShort(VideoCallActivity.this, " 通道建立完成，可语音咯 ");
                } else {
                    ELog.showToastShort(VideoCallActivity.this, " 通道建立完成，画面出来咯 ");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 视频、语音请求开启
     */
    public void callOpen() {
        devSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                /**
                 * 拨打视频
                 *
                 * @param uid:       与设备通信时使用的ID
                 * @param surface:   视频播放view
                 * @param devType:   设备类型
                 * @param frameRate: 视频帧率，默认15
                 *
                 * @param useOnlyVoice:          true:纯语音数据通道，false：音视频数据通道
                 * @param callType:              门内外类型 0：门外（单项视频）  1：门内（双向视频）
                 * @param isSupportDoubleTalk:   true：双向语音，false：语音单通道
                 * @param isSupportDoubleVideo:  true：双向视频，false：视频单通道
                 */
                callId = icvss.equesOpenCall(uid, holder.getSurface(), devType, 15, !hasVideo, callType, isSupportDoubleTalk, isSupportDoubleVideo); //视频 + 语音通话
                Log.e("callId",uid+" "+devType+" "+hasVideo+" "+callType+" "+isSupportDoubleTalk+" "+isSupportDoubleVideo);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    /**
     * 设置接听方式不同所展示UI
     */
    public void setAnsweringWayUI() {
        if (!hasVideo) {
            // 语音接听
            btnCapture.setVisibility(View.GONE);
            llCameraParent.setVisibility(View.GONE);
        } else {
            // 视频接听
            btnCapture.setVisibility(View.VISIBLE);
            // 室内来电双向通道
            if (callType == 1) {
                llCameraParent.setVisibility(View.VISIBLE);
                // 双向视频
                isSupportDoubleVideo = true;
                // 双向视频，开启手机前置摄像头预览
                setOnTouchSurfaceView();
                //phoneSurfaceView.setOnTouchListener(phoneTouchView);
                try {
                    ELog.e(TAG, " 创建AvEncoder对象... ");
                    // 创建AvEncoder对象
                    avcCodec = new AvcEncoder(mSurfviewHeight, mSurfviewWidth, framerate, biterate);// 宽高调换,解决客户端竖屏时候，设备显示横屏问题
                    // 启动编码线程
                    avcCodec.StartEncoderThread();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                llCameraParent.setVisibility(View.GONE);
            }
        }
        if (devType == BuddyType.TYPE_WIFI_DOOR_H5 || devType == BuddyType.TYPE_WIFI_DOOR_S1_PRO || devType == BuddyType.TYPE_WIFI_DOOR_S2) {
            // 双向对讲
            isSupportDoubleTalk = true;
            btnSoundSwitch.setText(getString(R.string.def_double_talk_hint));
            btnSoundSwitch.setOnClickListener(new MyOnClickListener());
            /*if (callType == 1) {
                // 双向视频
                isSupportDoubleVideo = true;
            }*/
        } else {
            // 长按说话
            btnSoundSwitch.setOnTouchListener(new MyOnTouchListener());
        }
        if (devType == BuddyType.TYPE_WIFI_DOOR_H5) {
            btnVoiReply.setVisibility(View.VISIBLE);
        } else {
            btnVoiReply.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!openPhoneCamera) {
            return;
        }
        byte[] yuv;
        if (this.avcCodec.isSemi()) {
            yuv = rotateYV12Degree270ToNV12(data, mSurfviewWidth, mSurfviewHeight);
        } else {
            yuv = rotateYV12Degree270To420P(data, mSurfviewWidth, mSurfviewHeight);
        }
        putYUVData(yuv, yuv.length);
    }

    /**
     * 将YV12(yyyyvvuu)旋转270度， 转换成NV12
     *
     * @param data
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    private byte[] rotateYV12Degree270ToNV12(byte[] data, int imageWidth, int imageHeight) {
        final int Y_SIZE = imageWidth * imageHeight;
        final int U_SIZE = imageWidth * imageHeight / 4;
        final int U_ROWS = imageHeight / 2;
        final int U_COLS = imageWidth / 2;
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

        int y_dest_pos = 0;
        int u_dest_pos = Y_SIZE;
        int v_dest_pos = Y_SIZE + U_SIZE;

        for (int x = imageWidth - 1; x >= 0; x--) {
            int yy = 0;
            for (int y = 0; y < imageHeight; y++) {
                yuv[y_dest_pos++] = data[yy + x];
                yy += imageWidth;
            }
        }

        for (int x = U_COLS - 1; x >= 0; x--) {
            int yy = 0;
            for (int y = 0; y < U_ROWS; y++) {
                yuv[u_dest_pos++] = data[Y_SIZE + U_SIZE + yy + x];
                yuv[u_dest_pos++] = data[Y_SIZE + yy + x];
                yy += U_COLS;
            }
        }
        return yuv;
    }

    /**
     * 将YV12旋转270度， 转换成YUV420P
     *
     * @param data
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    private byte[] rotateYV12Degree270To420P(byte[] data, int imageWidth, int imageHeight) {
        final int Y_SIZE = imageWidth * imageHeight;
        final int U_SIZE = imageWidth * imageHeight / 4;
        final int U_ROWS = imageHeight / 2;
        final int U_COLS = imageWidth / 2;
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

        int y_dest_pos = 0;
        int u_dest_pos = Y_SIZE;
        int v_dest_pos = Y_SIZE + U_SIZE;

        for (int x = imageWidth - 1; x >= 0; x--) {
            int yy = 0;
            for (int y = 0; y < imageHeight; y++) {
                yuv[y_dest_pos++] = data[yy + x];
                yy += imageWidth;
            }
        }

        for (int x = U_COLS - 1; x >= 0; x--) {
            int yy = 0;
            for (int y = 0; y < U_ROWS; y++) {
                yuv[v_dest_pos++] = data[Y_SIZE + yy + x];
                yuv[u_dest_pos++] = data[Y_SIZE + U_SIZE + yy + x];
                yy += U_COLS;
            }
        }
        return yuv;
    }

    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }

    /**
     * 开启手机摄像头预览
     */
    private void setOnTouchSurfaceView() {
        ELog.e(TAG, " setOnTouchSurfaceView() start... ");
        phoneSurfaceView.setZOrderMediaOverlay(true);
        telSurfaceHolder = phoneSurfaceView.getHolder();
        setSurfaceViewWH(screenWidthDip, screenHeightDip);
        telSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                ELog.e(TAG, " setOnTouchSurfaceView() surfaceCreated() S2、S1_Pro open phone camera... ");
                createCamera(telSurfaceHolder);
                startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                ELog.e(TAG, " setOnTouchSurfaceView() surfaceChanged start...");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                ELog.e(TAG, " setOnTouchSurfaceView() surfaceDestroyed start...");
                if (null != mCamera) {
                    telSurfaceHolder.removeCallback(this);
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    try {
                        mCamera.release();
                    } catch (Exception e) {
                    }
                    mCamera = null;
                }
            }
        });
    }

    /***********************本地摄像头*****************************/
    private AvcEncoder avcCodec;
    int mSurfviewWidth = 640;
    int mSurfviewHeight = 480;
    int framerate = 15;
    int biterate = 1024 * 400;

    public boolean createCamera(SurfaceHolder surfaceHolder) {
        try {
            if (Camera.getNumberOfCameras() < 2) {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            mCamera = Camera.open(mCameraId);
            if (mCamera == null) {
                ELog.e(TAG, "createCamera， mCamera == null start....");
                return false;
            }
            Camera.Parameters parameters = mCamera.getParameters();
            setCameraDisplayOrientation(this, mCameraId, mCamera);

            parameters.setPreviewFormat(ImageFormat.YV12);
            parameters.setPreviewSize(mSurfviewWidth, mSurfviewHeight);
            parameters.setPreviewFrameRate(15);
            // parameters.setPreviewFpsRange(1500, 1500);
            // 最小和最大帧设置预览,新api替换setPreviewFrameRate
            mCamera.setParameters(parameters);
            if (surfaceHolder != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
            }
            mCamera.setPreviewCallback(this);
            return true;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stack = sw.toString();
            destroyCamera();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 销毁Camera
     */
    public synchronized void destroyCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            try {
                mCamera.release();
            } catch (Exception e) {

            }
            mCamera = null;
            avcCodec.StopThread();
        }
    }

    /**
     * 开启预览
     */
    public synchronized void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
            try {
                mCamera.autoFocus(null);
            } catch (Exception e) {
                // 忽略异常
                ELog.i(TAG, "auto foucus fail");
            }

            int previewFormat = mCamera.getParameters().getPreviewFormat();
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            int size = previewSize.width * previewSize.height
                    * ImageFormat.getBitsPerPixel(previewFormat) / 8;
            // mCamera.addCallbackBuffer(new byte[size]);
            // mCamera.setPreviewCallbackWithBuffer(this);
        }
    }

    /**
     * 设置手机摄像头视频窗口
     */
    /*public void setTelSurfaceWindow() {
        if (openPhoneCamera) {//是否打开手机摄像
            openPhoneCamera();
        } else {
            closePhoneCamera();
        }
        try {
            setCameraDisplayOrientation(VideoCallActivity.this, mCameraId, mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        // 获得当前屏幕方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            // 若屏幕方向与水平轴负方向的夹角为0度
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            // 若屏幕方向与水平轴负方向的夹角为90度
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            // 若屏幕方向与水平轴负方向的夹角为180度
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            // 若屏幕方向与水平轴负方向的夹角为270度
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * 适配设备视频窗口
     */
    public void setSurfVWindow() {
        int viewH;
        int viewW;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) devSurfaceView.getLayoutParams();
        switch (devType) {
            case BuddyType.TYPE_CAMERA_C01:
                viewW = screenWidthDip;
                viewH = (screenWidthDip * 9) / 16;
                layoutParams.width = viewW;
                layoutParams.height = viewH;
                devSurfaceView.setLayoutParams(layoutParams);
                devSurfaceView.getHolder().setFixedSize(viewW, viewH);
                break;
            case BuddyType.TYPE_WIFI_DOOR_H5:
            case BuddyType.TYPE_WIFI_DOOR_S1_PRO:
            case BuddyType.TYPE_WIFI_DOOR_S2:
                if (screenWidthDip <= 960 && screenWidthDip > 540) {
                    viewH = (screenWidthDip * 12) / 16;
                } else if (screenWidthDip <= 540) {
                    viewH = (screenWidthDip * 13) / 16;
                } else {
                    viewH = (screenWidthDip * 11) / 16;
                }
                viewW = ((viewH * 16) / 9);
                // 上下视频有黑边,减去10dp
                viewH = viewH - 10;

                layoutParams.width = viewW;
                layoutParams.height = viewH;

                devSurfaceView.setLayoutParams(layoutParams);
                devSurfaceView.getHolder().setFixedSize(viewW, viewH);
                break;
            default:
                viewW = screenWidthDip;
                viewH = (screenWidthDip * 3) / 4;
                layoutParams.width = viewW;
                layoutParams.height = viewH;
                devSurfaceView.setLayoutParams(layoutParams);
                devSurfaceView.getHolder().setFixedSize(viewW, viewH);
                break;
        }
    }

    /**
     * 获取设备详情数据
     */

    public void getDevDetailsData() {
        if (detailsEntity != null) {
            snapSizeW = detailsEntity.getCamera_width();
            snapSizeW = 960;
            snapSizeH = detailsEntity.getCamera_height();
            snapSizeH = 1280;
        }
    }

    private void callSpeakerSetting(boolean f) {
        if (f) {
            btnSoundSwitch.setText("松开 结束");
            if (callId != null) {
                icvss.equesAudioRecordEnable(true, callId);
                icvss.equesAudioPlayEnable(false, callId);
            }
            closeSpeaker();
        } else {
            btnSoundSwitch.setText("按住 说话");

            if (callId != null) {
                icvss.equesAudioPlayEnable(true, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
            openSpeaker();
        }
    }

    /**
     * 双向对讲操作展示
     */
    private void callDoubleTalkSetting() {
        ELog.d(TAG, " callDoubleTalkSetting() isDoubleTalk: ", isDoubleTalk);
        if (!isDoubleTalk) {
            // 开启对讲
            btnSoundSwitch.setText(getString(R.string.def_press_double_talk_hint));
            if (callId != null) {
                icvss.equesAudioRecordEnable(true, callId);
                icvss.equesAudioPlayEnable(true, callId);
            }
            closeSpeaker();
        } else {
            // 关闭对讲
            btnSoundSwitch.setText(getString(R.string.def_double_talk_hint));
            if (callId != null) {
                icvss.equesAudioPlayEnable(true, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
            openSpeaker();
        }
    }

    private void setVideoSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidthDip = dm.widthPixels;
        screenHeightDip = dm.heightPixels;

        setAudioMute(); //设置是否静音
    }

    @Override
    public void onBackPressed() {
        hangUpCall();
    }


    @Override
    protected void onPause() {
        super.onPause();
        hangUpCall();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, defVolumeValue, 0);
        if (icvss != null) {
            // 变声回执默认原声
            icvss.equesChangeSound(0);
        }
        closeSpeaker();
        EventBus.getDefault().unregister(VideoCallActivity.this);

//        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 语音回复按压监听
     */
    private class MyOnTouchListener implements View.OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    callSpeakerSetting(true);
                    break;

                case MotionEvent.ACTION_UP:
                    callSpeakerSetting(false);
                    break;
            }
            return true;
        }
    }

    /**
     * 按钮监听事件
     */
    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId()==R.id.btn_hangupCall){
                hangUpCall();
            }else if (view.getId()==R.id.btn_change_dev_camera){
                // 切换设备预览摄像头（门内来电响应）
                icvss.equesCameraSwitch(uid);
            }else if (view.getId()==R.id.btn_switch_phone_camera){
                // 切换手机预览摄像头（门内来电响应）
                openPhoneCamera = !openPhoneCamera;
                if (openPhoneCamera) {
                    openPhoneCamera();
                } else {
                    closePhoneCamera();
                }
                icvss.equesSwitchPhone(uid, openPhoneCamera ? 1 : 2);
            }else if (view.getId()==R.id.btn_soundSwitch){
                // 双向对讲设备展示
                callDoubleTalkSetting();
                isDoubleTalk = !isDoubleTalk;
            }else if (view.getId()==R.id.btn_capture){
                String path = FileHelper.getCamPath();

                boolean isCreateOk = FileHelper.createDirectory(path);
                if (isCreateOk) {
                    path = StringUtils.join(path, "eques", ".jpg");

                    if (devType != BuddyType.TYPE_WIFI_DOOR_E600) {
                        snapSizeW = 0;
                        snapSizeH = 0;
                    }
                    icvss.equesSnapCapture(devType, path, snapSizeW, snapSizeH);
                    ELog.showToastLong(VideoCallActivity.this, "抓拍成功\n图片保存地址:" + path);
                } else {
                    ELog.showToastLong(VideoCallActivity.this, "抓拍失败");
                }
            }else if (view.getId()==R.id.btn_mute){
                if (callId != null) {
                    isMuteFlag = !isMuteFlag;

                    setAudioMute();//设置静音
                }
            }else if (view.getId()==R.id.btn_voi_reply){
                // 语音回复（随机回复，开发者根据相应参数进行展示设置操作）
                // 0：快递放在门口  1：外卖放在门口  2：请稍等，马上来开门  3：快递请放在快递柜
                icvss.equesVoiceReply(uid, 0);
            }else if (view.getId()==R.id.btn_change_sound){
                // 变声（原声、大叔）
                // 0：原声  1：大叔
                icvss.equesChangeSound(1);
            }
        }
    }

    /**
     * 打开手机摄像头
     */
    private void openPhoneCamera() {
        setSurfaceViewWH(screenWidthDip, screenHeightDip);
    }

    /**
     * 关闭手机摄像头
     */
    private void closePhoneCamera() {
        telSurfaceHolder.setFixedSize(1, 1);
    }

    /**
     * 设置手机预览摄像头成像视频宽高
     *
     * @param screenWidthDip
     * @param screenHeightDip
     */
    private void setSurfaceViewWH(int screenWidthDip, int screenHeightDip) {
        ELog.e(TAG, " setSurfaceViewWH() start screenWidthDip: ", screenWidthDip, " screenHeightDip: ", screenHeightDip);
        // 此处也可以根据横竖屏需求自己设置手机摄像头视频宽高
        int touchSurfaceViewWidth = Math.round(screenWidthDip / 4);
        int touchSurfaceViewHeight = Math.round(screenHeightDip / 5);
        ELog.e(TAG, " setSurfaceViewWH() touchSurfaceViewWidth: ", touchSurfaceViewWidth, " touchSurfaceViewHeight: ", touchSurfaceViewHeight);
        telSurfaceHolder.setFixedSize(touchSurfaceViewWidth, touchSurfaceViewHeight);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void openSpeaker() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            currVolumeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolumeValue,
                        0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSpeaker() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    currVolumeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC, currVolumeValue,
                            0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置静音或者外放
     */
    private void setAudioMute() {
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMuteFlag);

        if (isMuteFlag) {
            if (callId != null) {
                icvss.equesAudioPlayEnable(false, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }

            btnMute.setText("静音模式");

        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, defVolumeValue, 0);
            if (devType != BuddyType.TYPE_WIFI_DOOR_H5 && devType != BuddyType.TYPE_WIFI_DOOR_S1_PRO && devType != BuddyType.TYPE_WIFI_DOOR_S2) {
                callSpeakerSetting(false);
            }

            btnMute.setText("外放模式");
        }
    }

    /**
     * 关闭视频、语音
     */
    private void hangUpCall() {
        if (callId != null) {
            icvss.equesCloseCall(callId);
        }
        VideoCallActivity.this.finish();
    }

    /**
     * 移动窗口监听事件
     */
    /*private View.OnTouchListener phoneTouchView = new View.OnTouchListener() {
        int lastX, lastY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int ea = event.getAction();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    // 获取触摸事件触摸位置的原始X坐标
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                case MotionEvent.ACTION_MOVE:
                    //event.getRawX();获得移动的位置
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int l = v.getLeft() + dx;
                    int b = v.getBottom() + dy;
                    int r = v.getRight() + dx;
                    int t = v.getTop() + dy;

                    //下面判断移动是否超出屏幕
                    if (l < 0) {
                        l = 0;
                        r = l + v.getWidth();
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + v.getHeight();
                    }
                    if (r > screenWidth) {
                        r = screenWidth;
                        l = r - v.getWidth();
                    }
                    if (b > screenHeight) {
                        b = screenHeight;
                        t = b - v.getHeight();
                    }
                    v.layout(l, t, r, b);
                    ELog.e(TAG, "onTouch: ", " 左: ", l, " 上: ", t, " 右: ", r, " 下: ", b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };*/


}
