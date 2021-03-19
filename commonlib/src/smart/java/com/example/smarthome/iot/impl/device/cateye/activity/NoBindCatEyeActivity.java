package com.example.smarthome.iot.impl.device.cateye.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.example.smarthome.R;
import com.example.smarthome.iot.impl.device.cateye.dialog.WifiInputDialog;
import com.example.smarthome.iot.impl.device.cateye.utils.ICVSSUserModule;
import com.example.smarthome.iot.impl.device.cateye.utils.NetworkUtils;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.SPUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class NoBindCatEyeActivity extends BaseActivity implements View.OnClickListener, ICVSSListener {

    private LinearLayout top_back;
    private TextView top_title;
    private TextView top_btn;
    private WifiManager wifiManager;
    private String wifiSSID;
    private String preferfsKeyId="5d91e3b2b7fbb31c";
    private ImageView image_bitmap;
    private TextView tv_confirm_add_device;
    private ICVSSUserInstance icvss;
    private LinearLayout ll_pro;
    private TextView tv_1;
    private WifiInputDialog wifiInputDialog;
    private WifiInputDialog wifiInputDialog1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_bind_cat_eye);
        initView();
        initData();
    }

    private void initView(){
        top_back = findViewById(R.id.top_back);
        top_title = findViewById(R.id.top_title);
        top_btn = findViewById(R.id.top_btn);
        ll_pro = findViewById(R.id.ll_pro);
        top_back.setOnClickListener(this);

        image_bitmap = findViewById(R.id.image_bitmap);
        tv_confirm_add_device = findViewById(R.id.tv_confirm_add_device);
        tv_confirm_add_device.setOnClickListener(this);

        tv_1 = findViewById(R.id.tv_1);
    }
    private void initData(){
        top_title.setText("绑定猫眼");
        top_btn.setVisibility(View.GONE);
        tv_1.setText(Html.fromHtml("1."+"<font color=\"#598DF3\">长按5秒</font>"+"猫眼主机键"+"<font color=\"#598DF3\">松手</font>"));
        if (wifiManager==null){
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        wifiSSID = NetworkUtils.getCurrentWifiSSID(wifiManager);
        Log.e("未绑定页面wifi名称",wifiSSID);
        handler.sendEmptyMessageDelayed(110,100);
    }

    private String password="";



    private  Handler handler=new Handler(){

        WeakReference<Context> contextWeakReference=new WeakReference<Context>(NoBindCatEyeActivity.this);

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context activity=contextWeakReference.get();
            if (activity!=null){
                switch (msg.what){

                    case 110:
                        wifiInputDialog1 = new WifiInputDialog(NoBindCatEyeActivity.this,R.style.dialog, wifiSSID);
                        wifiInputDialog1.show();
                        wifiInputDialog1.setCanceledOnTouchOutside(true);
                        wifiInputDialog1.setCatEyeCallback(new WifiInputDialog.CatEyeCallback() {
                            @Override
                            public void callBack(String wifiPassword) {
                                password = wifiPassword;
                                String phone = SPUtils.get(NoBindCatEyeActivity.this, SpConstant.SP_USER_PHONE, "");
                                createQRCode(wifiSSID,password,preferfsKeyId,phone,BuddyType.TYPE_WIFI_DOOR_T21);
                            }
                        });
                        break;
                    case 111:
                        ll_pro.setVisibility(View.GONE);
                        finish();
                        break;
                }
            }


        }
    };

    private void createQRCode(String wifiSSID,String wifiPwd,String preferfsKeyId,String preferfsUserName,
                              int devType){
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        Bitmap bitmap = icvss.equesCreateQrcode(
                wifiSSID,
                wifiPwd,
                preferfsKeyId,
                preferfsUserName,
                devType,
                210);
        image_bitmap.setImageBitmap(bitmap);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.top_back){
            finish();
        }else if (v.getId()==R.id.tv_confirm_add_device){
            if (password==null||password.equals("")){
                Toast.makeText(NoBindCatEyeActivity.this,"wifi密码不能为空",Toast.LENGTH_LONG).show();
            }else {
                //确认添加设备
                String reqid = SPUtils.get(NoBindCatEyeActivity.this, "reqid", "");
                Log.e("reqid的数据",reqid);
                if (!reqid.equals("")) {
                    icvss.equesAckAddResponse(reqid, 1);
                    ll_pro.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(111, 10000);
                } else {
                    Toast.makeText(NoBindCatEyeActivity.this, "未添加电子猫眼或密码错误", Toast.LENGTH_LONG).show();
                }
            }
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

    }
}
