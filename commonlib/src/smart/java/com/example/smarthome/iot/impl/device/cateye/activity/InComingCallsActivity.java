package com.example.smarthome.iot.impl.device.cateye.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.ELog;
import com.eques.icvss.utils.Method;
import com.example.smarthome.R;
import com.example.smarthome.iot.HomeSmartFragment;
import com.example.smarthome.iot.impl.device.cateye.entity.EventBusEntity;
import com.example.smarthome.iot.impl.device.cateye.utils.FileHelper;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.example.smarthome.iot.impl.device.cateye.utils.ImageLoaderTask;
import com.xhwl.commonlib.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class InComingCallsActivity extends BaseActivity {
    
    public static final String TAG = "InComingCallsActivity";
    public final String PREVIEWPIC_LOCALPATH = "com.equessdk.app/InComingCalls/incoming_call_preview.jpg";
    public static final String EVENTBUS_ACTION_INCOMINGCALL_PREVIEWPIC = "eventBusActionInComingCallPreviewPic"; //eventBus 门铃图片消息
    
    private ICVSSUserInstance icvss; // ICVSS 对象
    private ImageView ivInComingCallPreviewPicture; // 来电预览图片
    private ImageLoaderTask imageLoaderTask; // 图片加载帮助类
    private String incomingSid; // 通话sessionId
    private String incomingBid; // 设备唯一值： bid
    private String incomingUid; // 设备通信id： uid
    private int devType;        // 设备类型
    private int callType;       // 设备门铃类型（1：门内 0：门外）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_calls);
        
        initUI();
        initData();
    }

    /**
     * EventBus 消息接收
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAsync(EventBusEntity busEntiy) {
        if(busEntiy == null){
            ELog.e(TAG, "InComingCallsActivityLog, onEventMainThread busEntiy == null!!!!");
            return;
        }
        String action = busEntiy.getAction();
        
        if(EVENTBUS_ACTION_INCOMINGCALL_PREVIEWPIC.equals(action)){//门铃图片
            String inComingCallPicfid = busEntiy.getInComingCallPicFid();
            
            String previewPicUrl = icvss.equesGetRingPictureUrl(inComingCallPicfid, incomingBid).toString();
            imageLoaderTask.execute(previewPicUrl);
        }
    }
    
    /**
     * 初始化UI
     */
    private void initUI() {
        ivInComingCallPreviewPicture = (ImageView) findViewById(R.id.iv_inComingCallPreviewPicture);
    }
    
    /**
     * 初始化 数据
     */
    private void initData() {
        icvss = ICVSSUserModule.getInstance(null).getIcvss();
        EventBus.getDefault().register(InComingCallsActivity.this);
        
        String previewPicPath = FileHelper.getRootFilePath() + PREVIEWPIC_LOCALPATH;
        imageLoaderTask = new ImageLoaderTask(InComingCallsActivity.this, ivInComingCallPreviewPicture, previewPicPath);
        
        Intent testActivityIntent = getIntent();
        incomingSid = testActivityIntent.getStringExtra(HomeSmartFragment.INCOMING_SID);
        incomingBid = testActivityIntent.getStringExtra(HomeSmartFragment.INCOMING_BID);
        incomingUid = testActivityIntent.getStringExtra(HomeSmartFragment.INCOMING_UID);
        devType = testActivityIntent.getIntExtra(Method.ATTR_ROLE, BuddyType.TYPE_WIFI_DOOR_R22);
        callType = testActivityIntent.getIntExtra(Method.METHOD_ATTR_CAMERATYPE, 0);
        ELog.e(TAG, " devType: ", devType, " callType: ", callType);
    }

    /**
     * 点击事件
     * @param v
     */
    public void onMyClick(View v) {
        Intent videoCallIntent = null;

        if (v.getId()==R.id.btn_hangUpInComingCall){
            if(incomingSid == null){
                ELog.e(TAG, "InComingCallsActivityLogs, incomingSid == null !!!!!");
            }else{
                ELog.i(TAG, "InComingCallsActivityLogs, equesCloseCall(incomingSid) start----->", incomingSid);
                icvss.equesCloseCall(incomingSid);
            }
        }else if (v.getId()==R.id.btn_VideoAnswering){
            ELog.e(TAG, " 视频接听 ");
            videoCallIntent = new Intent(this, VideoCallActivity.class);
            videoCallIntent.putExtra(Method.ATTR_BUDDY_UID, incomingUid);
            videoCallIntent.putExtra(Method.ATTR_ROLE, devType);
            videoCallIntent.putExtra(Method.METHOD_ATTR_CAMERATYPE, callType);
            videoCallIntent.putExtra(Method.ATTR_CALL_HASVIDEO, true); //是否显示视频， true：显示  false： 不显示
        }else if (v.getId()==R.id.btn_VoiceAnswering){//语音接听
            ELog.e(TAG, " 语音接听 ");
            videoCallIntent = new Intent(this, VideoCallActivity.class);
            videoCallIntent.putExtra(Method.ATTR_BUDDY_UID, incomingUid);
            videoCallIntent.putExtra(Method.ATTR_ROLE, devType);
            videoCallIntent.putExtra(Method.METHOD_ATTR_CAMERATYPE, callType);
            videoCallIntent.putExtra(Method.ATTR_CALL_HASVIDEO, false); //是否显示视频， true：显示  false： 不显示
        }
        
        if(videoCallIntent != null){
            startActivity(videoCallIntent);
        }
        InComingCallsActivity.this.finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        EventBus.getDefault().unregister(InComingCallsActivity.this);
    }
}
