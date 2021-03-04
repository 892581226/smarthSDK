package com.example.smarthome.iot.hkcateye;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;


public class AutoWifiPrepareStepOneActivity extends RootActivity implements View.OnClickListener {
    private Button btnNext;
    private String deviceType;
    private TextView topTip;
    private Button btnIntroduce;
    private ImageView imageBg;
    private AnimationDrawable aminBg;
    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private String mHKToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_wifi_prepare_step_one);
        addDestoryActivity(this,"AutoWifiPrepareStepOneActivity");

        initTitleBar();
        initUI();

    }

    private void initTitleBar() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText(R.string.auto_wifi_step_one_title);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initUI() {
        topTip = findViewById(R.id.topTip);
        imageBg = findViewById(R.id.imageBg);
        btnNext = findViewById(R.id.btnNext);
        btnIntroduce = findViewById(R.id.btnIntroduce);
        btnNext.setOnClickListener(this);
        btnIntroduce.setOnClickListener(this);

        topTip.setText(getString(R.string.tip_heard_voice));
        imageBg.setImageResource(R.drawable.video_camera1_3);
        btnNext.setText(R.string.autowifi_heard_voice);
        btnIntroduce.setText(R.string.autowifi_not_heard_voice);
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        int id = v.getId();
        if (id == R.id.btnNext){
            intent = new Intent(this, AutoWifiNetConfigActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        }else if(id == R.id.btnIntroduce){
            intent = new Intent(this, AutoWifiResetActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        }else if (id== R.id.top_back){
            finish();
        }
//        switch (v.getId()) {
//            case R.id.btnNext:
//                intent = new Intent(this, AutoWifiNetConfigActivity.class);
//                intent.putExtras(getIntent());
//                startActivity(intent);
//                break;
//            case R.id.btnIntroduce:
//                intent = new Intent(this, AutoWifiResetActivity.class);
//                intent.putExtras(getIntent());
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
    }

    @Override
    protected void onDestroy() {
        if (aminBg != null) {
            aminBg.stop();
            aminBg = null;
        }
        super.onDestroy();
    }

}