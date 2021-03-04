package com.yaokantv.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.smarthome.R;
import com.yaokantv.yaokansdk.callback.YaokanSDKListener;
import com.yaokantv.yaokansdk.manager.Yaokan;
import com.yaokantv.yaokansdk.model.MatchingData;
import com.yaokantv.yaokansdk.model.YkMessage;
import com.yaokantv.yaokansdk.model.e.MsgType;

import java.util.List;

/**
 * 一级匹配
 */
public class MatchActivity extends BaseActivity implements View.OnClickListener, YaokanSDKListener {
    int index = 0;
    List<MatchingData> dataList;
    MatchingData mMatch;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        initToolbar(R.string.first_match);
        textView = findViewById(R.id.tips);
        Yaokan.instance().addSdkListener(this);
        showDlg();
        Yaokan.instance().getMatchingResult(YaoKanSDK.curTid, YaoKanSDK.curBid);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Yaokan.instance().removeSdkListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_pre) {
            index--;
            if (index < 0) {
                index = dataList.size() - 1;
            }
            setMatchMsg();
            match();
        } else if (id == R.id.btn_next) {
            index++;
            if (index >= dataList.size()) {
                index = 0;
            }
            setMatchMsg();
            match();
        } else if (id == R.id.btn_test) {
            match();
        } else if (id == R.id.btn_second) {
            if (mMatch != null) {
                YaoKanSDK.curGid = mMatch.getGid();
                YaoKanSDK.curBid = mMatch.getBid();
                startActivity(new Intent(this, SecondMatchActivity.class));
            }
        }
    }

    private void match() {
        if (mMatch != null) {
            Yaokan.instance().sendCmd(YaoKanSDK.curDid, mMatch.getRid(), mMatch.getCmd(), YaoKanSDK.curTid, null, null);
        }
    }

    private void setMatchMsg() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.ll_match1).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_second).setVisibility(View.VISIBLE);
                mMatch = dataList.get(index);
                String msg = YaoKanSDK.curBName + " " + YaoKanSDK.curTName + (index + 1);
                msg += "\n" + (index + 1) + "/" + dataList.size();
                textView.setText(msg);
            }
        });
    }

    @Override
    public void onReceiveMsg(MsgType msgType, YkMessage ykMessage) {
        switch (msgType) {
            case Matching:
                if (ykMessage != null && ykMessage.getData() != null && ykMessage.getData() instanceof List) {
                    dataList = (List) ykMessage.getData();
                    if (dataList != null && dataList.size() > 0) {
                        setMatchMsg();
                    }
                }
                dismiss();
                break;
        }
    }
}
