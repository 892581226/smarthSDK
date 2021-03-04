package com.example.smarthome.iot.hkcateye;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.hkcateye.catutil.ProgressView;
import com.example.smarthome.iot.hkcateye.catutil.X5WebView;
import com.example.smarthome.iot.net.Constant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;
import com.xhwl.commonlib.uiutils.ToastUtil;
import com.xhwl.commonlib.uiutils.dialog.BaseDialog;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.destoryActivity;

/**
 * author: ydm
 * date: 2020/11/5 9:16
 * description: h5海康猫眼
 * update:
 * version:
 */
public class AlreadyBindHYCatEyeActivity extends BaseActivity implements View.OnClickListener, BaseDialog.OnConfirmListener, BaseDialog.OnCancelListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBt;
    private BaseDialog mBaseDialog;
    private String url;
    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 200:
                    EventBus.getDefault().post(new UpdateFamilyEvent(true));
                    destoryActivity("ConfigWifiExecutingActivity");
                    destoryActivity("NoBindHYCatEyeActivity");
                    destoryActivity("AutoWifiPrepareStepOneActivity");
                    destoryActivity("AutoWifiNetConfigActivity");
                    destoryActivity("AutoWifiResetActivity");
                    destoryActivity("ManualChooseConfigWifiWayActivity");
                    destoryActivity("ManualInputDeviceHotspotInfoActivity");
                    ToastUtil.show(AlreadyBindHYCatEyeActivity.this,"删除成功");
                    finish();
                    break;
                case 202:
                    ToastUtil.show(AlreadyBindHYCatEyeActivity.this,"删除失败");
                    break;
                case 210:
                    /*Bundle bundle = getIntent().getExtras();
                    if (bundle!=null){
                        String content = bundle.getString(JPushInterface.EXTRA_EXTRA);
                       JSONObject object= JSON.parseObject(content);
                       if (bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE).contains("门铃")){
                            url= "https://es.ys7.com/video/h5/talk?token="+mAccessToken+"&deviceSerial="+object.getString("devSerial")+"&verifyCode="+object.getString("validateCode");
                            initWeb(url);
                        }else {
                            url= "https://es.ys7.com/video/h5/message?token="+mAccessToken+"&deviceSerial="+object.getString("devSerial")+"&verifyCode="+object.getString("validateCode");
                            initWeb(url);
                        }
                        Log.e("TAG", "handleMessage: "+url);
                    }else {*/
                        url="https://es.ys7.com/video/h5/home?token="+mAccessToken+"&deviceSerial="+mDeviceSerial+"&verifyCode="+deviceCode;
                        initWeb(url);
                    //}
                    break;
                case 1:
                    mLinear.setVisibility(View.GONE);
                    break;
                case 0:
                    mLinear.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    finish();
                    break;
            }
        }
    };
    private String mDeviceSerial;
    private String deviceCode;
    private String mAccessToken;
    private RelativeLayout mLinear;
    private PermissionRequest myRequest;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private X5WebView mWebView;
    private IX5WebChromeClient.CustomViewCallback xCustomViewCallback;
    private FrameLayout videoFullView;// 全屏时视频加载view
    private View xCustomView;
    private ProgressView mProgressview;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_bind_h_k_cat_eye);
        mDeviceSerial = SPUtils.get(this, "DEVICE_SERIAL","");
        deviceCode = SPUtils.get(this, "DEVICE_VERIFY_CODE","");
        initView();
        getHKToken();
        Log.e("TAG", "onCreate: "+mDeviceSerial+"---"+deviceCode );
        mBaseDialog = new BaseDialog(this, 1)
                .setTitleText("删除设备")
                .setInfoText("设备删除后会清除与设备相关的所有数据，若再次使用需重新添加设备")
                .setCancelVisity(true)
                .setConfirmListener(this)
                .setCancelListener(this);
        mBaseDialog.setCancelable(false);

    }

    private void initView() {
        mView = findViewById(R.id.view);
        videoFullView= findViewById(R.id.video_fullView);
        mWebView = findViewById(R.id.X5WebView);
        mTopBack = findViewById(R.id.top_back_new_cat);
        mTopTitle = findViewById(R.id.top_title_new_cat);
        mLinear = findViewById(R.id.title_lay_cat);
        mTopBt = findViewById(R.id.top_btn_new_cat);
        mTopBack.setOnClickListener(this);
        mTopBt.setOnClickListener(this);
        mTopBt.setText("删除设备");
        mTopTitle.setText("电子猫眼");

    }
    private void initWeb(String url) {
     //传入Activity
        //进度条
        int progressColor = 0xff0064EB;
        mProgressview = new ProgressView(this,progressColor);
        mProgressview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6));
        mWebView.addView(mProgressview);
        mWebView.loadUrl(url);
        mWebView.addJavascriptInterface(new AndroidInterface(this),"android");
        String userAgentString = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgentString+" "+"XHWLHouseOwner");

        mWebView.setWebChromeClient(mWebChromeClient);

}
    com.tencent.smtt.sdk.WebChromeClient mWebChromeClient= new com.tencent.smtt.sdk.WebChromeClient(){

        // 拦截全屏调用的方法

        @Override
        public void onProgressChanged(com.tencent.smtt.sdk.WebView webView, int percent) {
            super.onProgressChanged(webView, percent);
            mProgressview.setProgress(percent);
            if (percent > 40) {
                webView.setVisibility(View.VISIBLE);
            }
        }

        // 拦截全屏调用的方法
        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            AlreadyBindHYCatEyeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.e("my", "onShowCustomView----xCustomView:" + xCustomView);
            mWebView.setVisibility(View.INVISIBLE);
            mLinear.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
// 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            view.setVisibility(View.VISIBLE);
            videoFullView.addView(view);
            xCustomView = view;
            xCustomView.setVisibility(View.VISIBLE);
            xCustomViewCallback = callback;
            videoFullView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            Log.e("my", "onHideCustomView----xCustomView:" + xCustomView);
            if (xCustomView == null) {
// 不是全屏播放状态
                return;
            }
            AlreadyBindHYCatEyeActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            videoFullView.removeView(xCustomView);
            xCustomView = null;
            videoFullView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            mWebView.setVisibility(View.VISIBLE);
            mLinear.setVisibility(View.VISIBLE);
            mView.setVisibility(View.VISIBLE);
        }



    };

    public class AndroidInterface {

        private Handler deliver = new Handler(Looper.getMainLooper());
        private Context context;

        public AndroidInterface( Context context) {
            this.context = context;
        }
        //必须声明此注解

        @JavascriptInterface
        public void closeWebView(int i) {
            handler.sendEmptyMessage(3);
        }

        @JavascriptInterface
        public void paramsToWebview(int show){
            switch (show){
                case 1:
                    handler.sendEmptyMessage(1);
                    break;
                case 0:
                   handler.sendEmptyMessage(0);
                    break;
            }
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.top_back_new_cat) {
            destoryActivity("ConfigWifiExecutingActivity");
            destoryActivity("AddGatewayActivity");
            destoryActivity("AutoWifiPrepareStepOneActivity");
            destoryActivity("AutoWifiNetConfigActivity");
            destoryActivity("AutoWifiResetActivity");
            destoryActivity("ManualChooseConfigWifiWayActivity");
            destoryActivity("ManualInputDeviceHotspotInfoActivity");
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        } else if (id == R.id.top_btn_new_cat) {
            mBaseDialog.show();

        }





    }

    private void removeDev() {

        String familyId = SPUtils.get(this, "FamilyId", "");
        JSONObject obj = new JSONObject();
        obj.put("accessToken",mAccessToken);
        obj.put("dev_id",mDeviceSerial);
        obj.put("supplier","海康电子猫眼");
        obj.put("deviceSerial", mDeviceSerial);
        obj.put("ownFamily", familyId);
        obj.put("validateCode",deviceCode );
        OkGo.<String>post(Constant.HOST + Constant.HKRemove)
                .upJson(JSON.toJSONString(obj))
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        LogUtils.e("resp", resp.getState() + "=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {
                        if (resp.getErrorCode().equalsIgnoreCase("200")) {
                           handler.sendEmptyMessage(200);
                        } else {
                            handler.sendEmptyMessage(202);
                        }

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        //loadingDialog.dismiss();
                        LogUtils.e("onComplete", "============");
                    }
                });

    }
    private void getHKToken() {
        OkGo.<String>get(Constant.HOST + Constant.GetToken)
//                .headers("aaa", "111")//
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept", disposable.isDisposed() + "===");
                    }
                })//
                .map(new Function<Response<String>, String>() {

                    @Override
                    public String apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply", "====" + stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        JSONObject jsonObject = JSON.parseObject(resp.getResult());
                        String accessToken = jsonObject.getString("accessToken");
                        return accessToken;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String token) {
                        mAccessToken = token;
                        handler.sendEmptyMessage(210);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    @Override
    public void onConfirmClick() {
        mBaseDialog.dismiss();
        removeDev();
    }

    @Override
    public void onCancelListener() {
        if (mBaseDialog.isShowing()) {
            mBaseDialog.dismiss();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }else {
                destoryActivity("ConfigWifiExecutingActivity");
                destoryActivity("AddGatewayActivity");
                destoryActivity("AutoWifiPrepareStepOneActivity");
                destoryActivity("AutoWifiNetConfigActivity");
                destoryActivity("AutoWifiResetActivity");
                destoryActivity("ManualChooseConfigWifiWayActivity");
                destoryActivity("ManualInputDeviceHotspotInfoActivity");
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();

    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }
}