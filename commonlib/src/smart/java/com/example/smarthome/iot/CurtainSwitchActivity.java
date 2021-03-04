package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceInfoVo;
import com.example.smarthome.iot.entry.SmartControlVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.HilinkDeviceControl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.constant.SpConstant;
import com.xhwl.commonlib.uiutils.LogUtils;
import com.xhwl.commonlib.uiutils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

/**
 * author: glq
 * date: 2019/5/28 15:10
 * description: 海令窗帘面板
 * update: 2019/5/28
 * version:
 */
public class CurtainSwitchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mCurtainOpen;
    private ImageView mCurtainStop;
    private ImageView mCurtainClose;

    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private String familyId, userId;
    private TextView mTopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_switch);
        addDestoryActivity(this, "CurtainSwitchActivity");
        EventBus.getDefault().register(this);
        initView();
        initDate();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(UpdateFamilyEvent messageEvent) {
        if (messageEvent.isUpdate()) {
            mTopTitle.setText(messageEvent.getTitle());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    private void initDate() {
        mTopBtn.setText("管理");
        deviceInfoBean = getIntent().getParcelableExtra("deviceItem");
        Log.e("首页电动窗帘",deviceInfoBean.toString());
        mTopTitle.setText(deviceInfoBean.getDeviceName());
        userId = SPUtils.get(this, SpConstant.SP_USER_TELEPHONE, "");
        familyId = SPUtils.get(this, "familyId" + userId, "");
    }

    private void initView() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle =  findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mCurtainOpen = (ImageView) findViewById(R.id.curtain_open);
        mCurtainOpen.setOnClickListener(this);
        mCurtainStop = (ImageView) findViewById(R.id.curtain_stop);
        mCurtainStop.setOnClickListener(this);
        mCurtainClose = (ImageView) findViewById(R.id.curtain_close);
        mCurtainClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_back) {
            finish();
        } else if (v.getId() == R.id.top_btn) {
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean", deviceInfoBean);
            startActivity(intent);
        } else if (v.getId() == R.id.curtain_open) {
            resetCurtain();
            mCurtainOpen.setImageResource(R.drawable.icon_iot_curtain_open_check);
            // 窗帘开
            controlCurtain(HilinkDeviceControl.CMD_CURTAIN_ON);
        } else if (v.getId() == R.id.curtain_stop) {
            resetCurtain();
            mCurtainStop.setImageResource(R.drawable.icon_iot_curtain_stop_check);
            // 窗帘暂停
            controlCurtain(HilinkDeviceControl.CMD_CURTAIN_STOP);
        } else if (v.getId() == R.id.curtain_close) {
            resetCurtain();
            mCurtainClose.setImageResource(R.drawable.icon_iot_curtain_close_check);
            // 窗帘关
            controlCurtain(HilinkDeviceControl.CMD_CURTAIN_OFF);
        }

    }

    private void controlCurtain(String order) {
        String encodeCmd = Base64.encodeToString(order.getBytes(), Base64.NO_WRAP);
        DeviceInfoVo deviceInfoVo = new DeviceInfoVo();
        deviceInfoVo.setDeviceId(deviceInfoBean.getDeviceId());//deviceInfoBean.getDeviceId()
        deviceInfoVo.setDeviceType(deviceInfoBean.getDeviceType());
        deviceInfoVo.setGatewayId(deviceInfoBean.getGatewayId());
        deviceInfoVo.setSupplierId(deviceInfoBean.getSupplierId());
        SmartControlVo smartControlVo = new SmartControlVo();
        smartControlVo.setUserId(userId);
        smartControlVo.setFamilyId(familyId);
        smartControlVo.setCmdType("control");
        smartControlVo.setCmd(encodeCmd);
        smartControlVo.setDeviceInfo(deviceInfoVo);

        OkGo.<String>post(Constant.HOST + Constant.Device_control)
                .upJson(JSON.toJSONString(smartControlVo))
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
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

                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete", "============");
                    }
                });
    }

    private void resetCurtain() {
        mCurtainOpen.setImageResource(R.drawable.icon_iot_curtain_open_uncheck);
        mCurtainStop.setImageResource(R.drawable.icon_iot_curtain_stop_uncheck);
        mCurtainClose.setImageResource(R.drawable.icon_iot_curtain_close_uncheck);
    }
}
