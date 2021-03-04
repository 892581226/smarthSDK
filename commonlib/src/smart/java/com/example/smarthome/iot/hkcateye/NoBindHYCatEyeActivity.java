package com.example.smarthome.iot.hkcateye;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.entry.ZxingConfig;
import com.example.smarthome.iot.hkcateye.catutil.ApConfigWifiPresenter;
import com.example.smarthome.iot.hkcateye.catutil.ConfigWifiExecutingActivityPresenter;
import com.example.smarthome.iot.hkcateye.catutil.Constant;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;
import com.example.smarthome.iot.yscamera.YsCaptureActivity;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class NoBindHYCatEyeActivity extends RootActivity implements View.OnClickListener {
    public static final String BUNDE_SERIANO = "SerialNo";
    public static final String BUNDE_VERYCODE = "very_code";
    private String TAG= NoBindHYCatEyeActivity.class.getSimpleName();
    private EditText mVerifyCodeEt;
    private EditText mSeriesNumberEt;
    private Button mBt1;
    private String mSerialNoStr;
    private String mSerialVeryCodeStr;
    private TextView mCapterId;
    private int REQUEST_CODE_SCAN = 100;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private String deviceType = "";
    private String mDevCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hk_gateway);
        addDestoryActivity(this,"NoBindHYCatEyeActivity");
        ConfigWifiExecutingActivityPresenter.addPresenter(new ApConfigWifiPresenter());
        init();
    }

    private void init() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mCapterId = findViewById(R.id.top_btn);
        mTopTitle.setText("配置网络");
        mCapterId.setText("二维码扫描");
        mCapterId.setVisibility(View.GONE);
        mSeriesNumberEt = findViewById(R.id.seriesNumberEt);
        mVerifyCodeEt = findViewById(R.id.verifycodeEt);
        mBt1=findViewById(R.id.search);
        mCapterId.setOnClickListener(this);
        mBt1.setOnClickListener(this);
        mDevCode ="CS-DP1C-4A1WPFBSR";
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id== R.id.search){
            mSerialNoStr = mSeriesNumberEt.getText().toString().trim();
            mSerialVeryCodeStr = mVerifyCodeEt.getText().toString().trim();
            if (!mSerialNoStr.isEmpty()&&!mSerialVeryCodeStr.isEmpty()){
                Intent intent;
                intent = new Intent(NoBindHYCatEyeActivity.this, AutoWifiPrepareStepOneActivity.class);
                intent.putExtra("devCode", mDevCode);
                intent.putExtra(BUNDE_SERIANO, mSerialNoStr);
                intent.putExtra(BUNDE_VERYCODE,mSerialVeryCodeStr);
                startActivity(intent);
            }else {
                toast("设备序列号或验证码不能为空");
            }

        }else if(id== R.id.top_btn){
            Intent mIntent = new Intent();
            ZxingConfig config = new ZxingConfig();
            config.setShowbottomLayout(false);//底部布局（包括闪光灯和相册）
            config.setPlayBeep(true);//是否播放提示音
            config.setShake(true);//是否震动
            mIntent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            mIntent.setClass(this, YsCaptureActivity.class);
            startActivityForResult(mIntent, REQUEST_CODE_SCAN);
        }else if (id== R.id.top_back){
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String code = data.getStringExtra(Constant.CODED_CONTENT);
            Log.e("TAG", "onActivityResult: "+ code);
            handleDecode(code);
        }
    }


    public void handleDecode(String resultString) {
        mSerialNoStr = "";
        mSerialVeryCodeStr = "";
        deviceType = "";
        // CS-F1-1WPFR
        // CS-A1-1WPFR
        // CS-C1-1FPFR
        // resultString = "www.xxx.com\n456654855\nABCDEF\nCS-C3-21PPFR\n";
        // 字符集合
        String[] newlineCharacterSet = {
                "\n\r", "\r\n", "\r", "\n"};
        String stringOrigin = resultString;
        // 寻找第一次出现的位置
        int a = -1;
        int firstLength = 1;
        for (String string : newlineCharacterSet) {
            if (a == -1) {
                a = resultString.indexOf(string);
                if (a > stringOrigin.length() - 3) {
                    a = -1;
                }
                if (a != -1) {
                    firstLength = string.length();
                }
            }
        }

        // 扣去第一次出现回车的字符串后，剩余的是第二行以及以后的
        if (a != -1) {
            resultString = resultString.substring(a + firstLength);
        }
        // 寻找最后一次出现的位置
        int b = -1;
        for (String string : newlineCharacterSet) {
            if (b == -1) {
                b = resultString.indexOf(string);
                if (b != -1) {
                    mSerialNoStr = resultString.substring(0, b);
                    firstLength = string.length();
                }
            }
        }

        // 寻找遗失的验证码阶段
        if (mSerialNoStr != null && b != -1 && (b + firstLength) <= resultString.length()) {
            resultString = resultString.substring(b + firstLength);
        }

        // 再次寻找回车键最后一次出现的位置
        int c = -1;
        for (String string : newlineCharacterSet) {
            if (c == -1) {
                c = resultString.indexOf(string);
                if (c != -1) {
                    mSerialVeryCodeStr = resultString.substring(0, c);
                }
            }
        }

        // 寻找CS-C2-21WPFR 判断是否支持wifi
        if (mSerialNoStr != null && c != -1 && (c + firstLength) <= resultString.length()) {
            resultString = resultString.substring(c + firstLength);
        }
        if (resultString != null && resultString.length() > 0) {
            deviceType = resultString;
        }

        if (b == -1) {
            mSerialNoStr = resultString;
        }

        if (mSerialNoStr == null) {
            mSerialNoStr = stringOrigin;
        }
        LogUtils.e(TAG, "handleDecode: "+resultString+"----"+mSerialNoStr+"--"+mSerialVeryCodeStr );
        ConfigWifiExecutingActivityPresenter.addPresenter(new ApConfigWifiPresenter());
        if (mSerialNoStr.length()==9&&mSerialVeryCodeStr.length()==6) {
            Intent mIntent = new Intent();
            mIntent.setClass(NoBindHYCatEyeActivity.this, AutoWifiPrepareStepOneActivity.class);
            mIntent.putExtra(BUNDE_SERIANO, mSerialNoStr);
            mIntent.putExtra(BUNDE_VERYCODE, mSerialVeryCodeStr);
            mIntent.putExtra("devCode",resultString);
            startActivity(mIntent);
        }else {
            ToastUtil.show("请扫描有效二维码");
        }
    }
}