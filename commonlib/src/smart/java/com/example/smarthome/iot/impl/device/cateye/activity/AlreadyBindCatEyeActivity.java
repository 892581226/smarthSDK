package com.example.smarthome.iot.impl.device.cateye.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.Method;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.cateye.dialog.DeteleCatEyeDialog;
import com.example.smarthome.iot.impl.device.cateye.entity.EventBusEntity;
import com.example.smarthome.iot.impl.device.cateye.utils.Constants;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlreadyBindCatEyeActivity extends BaseActivity implements View.OnClickListener, ICVSSListener {

    private LinearLayout top_back;
    private TextView top_title;
    private TextView top_btn;
    private DeteleCatEyeDialog catEyeDialog;
    private ImageView image_to_call;
    private ICVSSUserInstance icvss;
    private String uid;
    private String bid;
    private LinearLayout ll_pro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_bind_cat_eye);
        initView();
        initData();
        icvss.equesWakeUp(uid);
        handler.sendEmptyMessageAtTime(110,5000);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 110:
                    icvss.equesWakeUp(uid);
                    handler.sendEmptyMessageDelayed(110,5000);
                    break;
            }
        }
    };

    private void initView() {
        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        top_back.setOnClickListener(this);
        top_btn.setOnClickListener(this);
        image_to_call = findViewById(R.id.image_to_call);
        image_to_call.setOnClickListener(this);
        ll_pro = findViewById(R.id.ll_pro);
    }

    private void initData() {
        top_title.setText("电子猫眼");
        top_btn.setText("删除");
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        String phone= SPUtils.get(AlreadyBindCatEyeActivity.this,SpConstant.SP_USER_PHONE,"");
//        icvss.equesLogin(AlreadyBindCatEyeActivity.this,Constants.DISTRIBUTE_URL,phone,"sdk_demo");
        uid=SPUtils.get(AlreadyBindCatEyeActivity.this,"uid","");
        bid=SPUtils.get(AlreadyBindCatEyeActivity.this,"bid","");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 删除设备
     */
    private void removeDev(){
        ll_pro.setVisibility(View.GONE);
        icvss.equesDelDevice(uid);
        finish();
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.top_back){
            finish();
        }else if (v.getId()==R.id.top_btn){
            catEyeDialog = new DeteleCatEyeDialog(this,R.style.dialog);
            catEyeDialog.show();
            catEyeDialog.setCatEyeCallback(new DeteleCatEyeDialog.DeteleCatEyeCallback() {
                @Override
                public void callBack() {
                    //删除设备
                    ll_pro.setVisibility(View.VISIBLE);
                    removeDev();
                }
            });
        }else if (v.getId()==R.id.image_to_call){
            //点击呼叫视频
            EventBus.getDefault().post(new EventBusEntity(Constants.EVENTBUS_DISPATCH_CHANNEL_SETUP_SUCCESSFUL, 4000));
            Intent intent = new Intent(AlreadyBindCatEyeActivity.this, VideoCallActivity.class);
            intent.putExtra(Method.ATTR_ROLE, BuddyType.TYPE_WIFI_DOOR_T21);
            intent.putExtra(Method.ATTR_BUDDY_UID, uid);
            intent.putExtra(Method.ATTR_CALL_HASVIDEO, true); //是否显示视频， true：显示  false： 不显示
//            intent.putExtra("devDetailsEntity", detailsEntity);
            startActivity(intent);
        }
    }

    @Override
    public void onDisconnect(int i) {

    }

    @Override
    public void onPingPong(int i) {

    }

    @Override
    public void onMeaasgeResponse(JSONObject jsonObject) {
        String method=jsonObject.optString(Method.METHOD);
        int code=jsonObject.optInt(Method.ATTR_ERROR_CODE);
        Log.e("点击呼叫页面",jsonObject.toString());
        if (method.equals("login")&&code==4000){
            Log.e("智能家居数据","method:"+method+"code:"+code);
            icvss.equesGetDeviceList();
        }else
        if (method.equals(Method.METHOD_BDYLIST)){
            JSONArray onlines = jsonObject.optJSONArray(Method.ATTR_ONLINES);
            JSONObject onlinesObj = onlines.optJSONObject(0);
            if (onlinesObj != null) {
                uid = onlinesObj.optString(Method.ATTR_BUDDY_UID, null);
                bid = onlinesObj.optString(Method.ATTR_BUDDY_BID,null);
                if (uid != null) {
                    Log.e("uid",uid);
                }
            } else {
                // 一般不在线状态下没有此数据
            }
        }else if (method.equals(Method.METHOD_RMBDY_RESULT)){
            //删除设备
            if (code==4000){
                ll_pro.setVisibility(View.GONE);
                Toast.makeText(AlreadyBindCatEyeActivity.this,"删除设备成功",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
