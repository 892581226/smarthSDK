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
import com.example.smarthome.iot.entry.DeviceRealStateVo;
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

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

public class DoubleCurtainSwitchActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mCurtainOpen;
    private ImageView mCurtainStop;
    private ImageView mCurtainClose;
    private ImageView mDoubleCurtainOpen;
    private ImageView mDoubleCurtainStop;
    private ImageView mDoubleCurtainClose;

    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;
    private String familyId, userId;
    private TextView mTopBtn;
    private List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_curtain_switch);
        addDestoryActivity(this, "Activity");
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
        init(deviceInfoBean.getDeviceId(), deviceInfoBean.getDeviceType(), deviceInfoBean.getSupplierId());
    }

    private void initView() {
        mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mCurtainOpen = (ImageView) findViewById(R.id.curtain_open);
        mCurtainOpen.setOnClickListener(this);
        mCurtainStop = (ImageView) findViewById(R.id.curtain_stop);
        mCurtainStop.setOnClickListener(this);
        mCurtainClose = (ImageView) findViewById(R.id.curtain_close);
        mCurtainClose.setOnClickListener(this);

        mDoubleCurtainOpen = (ImageView) findViewById(R.id.double_curtain_open);
        mDoubleCurtainOpen.setOnClickListener(this);
        mDoubleCurtainStop = (ImageView) findViewById(R.id.double_curtain_stop);
        mDoubleCurtainStop.setOnClickListener(this);
        mDoubleCurtainClose = (ImageView) findViewById(R.id.double_curtain_close);
        mDoubleCurtainClose.setOnClickListener(this);
    }

    private void init(String deviceId, String deviceType, String supplierId) {
        OkGo.<String>get(Constant.HOST+ Constant.Device_getRealMsg)
                .params("deviceId",deviceId)
                .params("deviceType",deviceType)
                .params("supplierId",supplierId)
                .converter(new StringConvert())//该方法是网络请求的转换器，功能是将网络请求的结果转换成我们需要的数据类型
                .adapt(new ObservableResponse<String>())//该方法是方法返回值的适配器，功能是将网络请求的Call<T>对象，适配成我们需要的Observable<T>对象
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
//                        showLoading();
                        LogUtils.e("accept",disposable.isDisposed()+"===");
                    }
                })
                .map(new Function<Response<String>, DeviceRealStateVo>() {
                    @Override
                    public DeviceRealStateVo apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);
                        DeviceRealStateVo obj = null;
                        if(resp!=null && resp.getErrorCode().equalsIgnoreCase("200")){
                            obj = JSON.parseObject(resp.getResult(), DeviceRealStateVo.class);
                        }

                        LogUtils.e("resp",resp.getState()+"=====");
                        return obj;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<DeviceRealStateVo>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","========---====");
                    }

                    @Override
                    public void onNext(DeviceRealStateVo realStateVo) {
                        List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosHongYan = realStateVo.getRealState().getStateInfos();
                        Log.e("智能插座", "ssss="+stateInfosHongYan);
                        if(realStateVo!=null &&stateInfosHongYan!=null && stateInfosHongYan.size()>0){
                            stateInfosBeans =stateInfosHongYan;
                            initSwitch(stateInfosBeans);
                            //initSwitch(stateInfosBeans);
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete","============");
                    }
                });

    }

    private void initSwitch(List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans) {
        for (DeviceRealStateVo.RealStateBean.StateInfosBean datas:stateInfosBeans){
            switch (datas.getDev_ep_id()){
                default:
                    break;
                case 1:
                    if (datas.getState().contains("0")){
                        mCurtainClose.setImageResource(R.drawable.icon_iot_double_curtain_close_check);
                    }else if (datas.getState().contains("1")){
                        mCurtainOpen.setImageResource(R.drawable.icon_iot_double_curtain_open_check);
                    }else if (datas.getState().contains("2")){
                        mCurtainStop.setImageResource(R.drawable.icon_iot_double_curtain_stop_check);
                    }

                    break;

                case 2:

                    if (datas.getState().contains("0")){
                        mDoubleCurtainClose.setImageResource(R.drawable.icon_iot_double_curtain_close_check);
                    }else if (datas.getState().contains("1")){
                        mDoubleCurtainOpen.setImageResource(R.drawable.icon_iot_double_curtain_open_check);
                    }else if (datas.getState().contains("2")){
                        mDoubleCurtainStop.setImageResource(R.drawable.icon_iot_double_curtain_stop_check);
                    }

                    break;


            }

        }

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
            mCurtainOpen.setImageResource(R.drawable.icon_iot_double_curtain_open_check);
            // 窗帘开
            controlCurtain(HilinkDeviceControl.DOUBLE_CMD_CURTAIN_ON);
        } else if (v.getId() == R.id.curtain_stop) {
            resetCurtain();
            mCurtainStop.setImageResource(R.drawable.icon_iot_double_curtain_stop_check);
            // 窗帘暂停
            controlCurtain(HilinkDeviceControl.DOUBLE_CMD_CURTAIN_STOP);
        } else if (v.getId() == R.id.curtain_close) {
            resetCurtain();
            mCurtainClose.setImageResource(R.drawable.icon_iot_double_curtain_close_check);
            // 窗帘关
            controlCurtain(HilinkDeviceControl.DOUBLE_CMD_CURTAIN_OFF);
        } else if (v.getId() == R.id.double_curtain_open) {
            resetDoubleCurtain();
            mDoubleCurtainOpen.setImageResource(R.drawable.icon_iot_double_curtain_open_check);
            // 窗帘开
            controlCurtain(HilinkDeviceControl.DOUBLE2_CMD_CURTAIN_ON);
        } else if (v.getId() == R.id.double_curtain_stop) {
            resetDoubleCurtain();
            mDoubleCurtainStop.setImageResource(R.drawable.icon_iot_double_curtain_stop_check);
            // 窗帘暂停
            controlCurtain(HilinkDeviceControl.DOUBLE2_CMD_CURTAIN_STOP);
        } else if (v.getId() == R.id.double_curtain_close) {
            resetDoubleCurtain();
            mDoubleCurtainClose.setImageResource(R.drawable.icon_iot_double_curtain_close_check);
            // 窗帘关
            controlCurtain(HilinkDeviceControl.DOUBLE2_CMD_CURTAIN_OFF);
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
                    public void accept(@NonNull Disposable disposable) throws Exception {
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
                    public void onSubscribe(@NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe", "========---====");
                    }

                    @Override
                    public void onNext(CommonResp resp) {

                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
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
        mCurtainOpen.setImageResource(R.drawable.icon_iot_double_curtain_open_uncheck);
        mCurtainStop.setImageResource(R.drawable.icon_iot_double_curtain_stop_uncheck);
        mCurtainClose.setImageResource(R.drawable.icon_iot_double_curtain_close_uncheck);
    }

    private void resetDoubleCurtain() {
        mDoubleCurtainOpen.setImageResource(R.drawable.icon_iot_double_curtain_open_uncheck);
        mDoubleCurtainStop.setImageResource(R.drawable.icon_iot_double_curtain_stop_uncheck);
        mDoubleCurtainClose.setImageResource(R.drawable.icon_iot_double_curtain_close_uncheck);
    }
}