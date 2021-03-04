//package com.example.smarthome.iot.yscamera;
//
//import android.os.Bundle;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.example.commonlib.base.BaseActivity;
//import com.example.commonlib.base.uiutils.LogUtils;
//import com.example.commonlib.base.uiutils.SPUtils;
//import com.example.commonlib.base.uiutils.StringUtils;
//import com.example.smarthome.R;
//import com.example.smarthome.iot.adapter.DeviceListAdapter;
//import com.example.smarthome.iot.entry.SmartInfoVo;
//import com.ezvizuikit.open.EZUIError;
//import com.ezvizuikit.open.EZUIKit;
//import com.ezvizuikit.open.EZUIPlayer;
//import com.zhy.autolayout.AutoLinearLayout;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//
///**
// * author:
// * date:
// * description:
// * update:
// * version:
// */
//public class YsCameraUikitActivity extends BaseActivity implements View.OnClickListener {
//
//    private AutoLinearLayout mTopBack;
//    private TextView mTopTitle;
//    private TextView mTopBtn;
//    private View mTitleLine;
//
//    private EZUIPlayer mPlayer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ys_uikit);
//        initView();
//        initDate();
//    }
//
//    private void initDate() {
//        mTitleLine.setVisibility(View.GONE);
//        mTopBtn.setText("管理");
//        //初始化EZUIKit
//        EZUIKit.initWithAppKey(getApplication(),"e24bec0228ad4f41bf7f0586d23d9ebe");
//
//        //设置授权token
//        EZUIKit.setAccessToken("at.c4bren4tbp4jok6n4fbil11v0t6by8qk-3d2qaq0st3-0y0hdt6-5pvswwvv8");
//
//        EZUIPlayer.EZUIPlayerCallBack callBack = new EZUIPlayer.EZUIPlayerCallBack() {
//            @Override
//            public void onPlaySuccess() {
//                LogUtils.e("onPlaySuccess","================");
//            }
//
//            @Override
//            public void onPlayFail(EZUIError ezuiError) {
//                LogUtils.e("onPlayFail",ezuiError.getErrorString()+"-------------");
//            }
//
//            @Override
//            public void onVideoSizeChange(int i, int i1) {
//                LogUtils.e("onVideoSizeChange",i+"-------------"+i1);
//            }
//
//            @Override
//            public void onPrepared() {
//                LogUtils.e("onPrepared","-------------");
//                //开始播放
//                mPlayer.startPlay();
//            }
//
//            @Override
//            public void onPlayTime(Calendar calendar) {
//                LogUtils.e("onPlayTime",calendar.toString()+"-------------");
//            }
//
//            @Override
//            public void onPlayFinish() {
//                LogUtils.e("onPlayFinish","======================");
//            }
//        };
//
//        //设置播放回调callback
//        mPlayer.setCallBack(callBack);
//
//        //设置播放参数
//        mPlayer.setUrl("ezopen://KRCQDY@open.ys7.com/137234674/1.hd.live");
//        //创建loadingview
//        ProgressBar mLoadView = new ProgressBar(this);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//        mLoadView.setLayoutParams(lp);
//        //设置loadingview
//        mPlayer.setLoadingView(mLoadView);
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
//        //获取EZUIPlayer实例
//        mPlayer = (EZUIPlayer) findViewById(R.id.player_ui);
//
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
////                Intent intent = new Intent(this, DeviceInfoActivity.class);
////                intent.putExtra("deviceInfoBean",deviceInfoBean);
////                startActivity(intent);
//        }
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        //停止播放
//        mPlayer.stopPlay();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //释放资源
//        mPlayer.releasePlayer();
//    }
//}
