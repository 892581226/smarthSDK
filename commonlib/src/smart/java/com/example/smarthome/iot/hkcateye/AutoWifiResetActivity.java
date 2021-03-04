package com.example.smarthome.iot.hkcateye;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.iot.hkcateye.catutil.RootActivity;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;


public class AutoWifiResetActivity extends RootActivity implements View.OnClickListener {
    private View btnNext;
    private TextView topTip;
    private LinearLayout mTopBack;
    private TextView mTopTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_wifi_reset);
        addDestoryActivity(this,"AutoWifiResetActivity");
        initTitleBar();
        initUI();
    }

    private void initTitleBar() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText(R.string.device_reset_title);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


        private void initUI() {
        topTip = findViewById(R.id.topTip);
        btnNext = findViewById(R.id.btnNext);
        topTip.setText(R.string.device_reset_tip);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        if (id == R.id.btnNext){
            intent = new Intent(this, AutoWifiNetConfigActivity.class);
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
//            default:
//                break;
//        }
    }

}