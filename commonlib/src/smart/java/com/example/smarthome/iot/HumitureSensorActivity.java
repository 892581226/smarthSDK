package com.example.smarthome.iot;

import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.smarthome.R;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.SensorInfoVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xhwl.commonlib.application.MyAPP.addDestoryActivity;

/**
 * author: glq
 * date: 2019/5/27 10:54
 * description: 海令智能温湿度传感器
 * update: 2019/5/27
 * version:
 */
public class HumitureSensorActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mTopBack;
    private TextView mTopTitle;
    /**
     * 28℃
     */
    private TextView mTempSensorText;
    /**
     * 温度 舒适
     */
    private TextView mTempSensorTip;
    private ConstraintLayout mTempSensorImg;
    /**
     * 70%
     */
    private TextView mHumiditySensorText;
    /**
     * 湿度 舒适
     */
    private TextView mHumiditySensorTip;
    private ConstraintLayout mHumiditySensorImg;
    private TextView mTopBtn;
    private SmartInfoVo.FamilysBean.DeviceInfoBean deviceInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humiture_sensor);
        addDestoryActivity(this,"HumitureSensorActivity");
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
        if(deviceInfoBean!=null){
            mTopTitle.setText(deviceInfoBean.getDeviceName());
            getSensorInfo();
        }
    }

    private void initView() {
        mTopBack =  findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTempSensorText = (TextView) findViewById(R.id.temp_sensor_text);
        mTempSensorTip = (TextView) findViewById(R.id.temp_sensor_tip);
        mTempSensorImg = (ConstraintLayout) findViewById(R.id.temp_sensor_img);
        mHumiditySensorText = (TextView) findViewById(R.id.humidity_sensor_text);
        mHumiditySensorTip = (TextView) findViewById(R.id.humidity_sensor_tip);
        mHumiditySensorImg = (ConstraintLayout) findViewById(R.id.humidity_sensor_img);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_back){
            finish();
        } else if(v.getId() == R.id.top_btn){
            // 管理
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",deviceInfoBean);
            startActivity(intent);
        }
    }

    private void getSensorInfo(){
        OkGo.<String>get(Constant.HOST+Constant.Device_getSensorInfo)
                .params("deviceId",deviceInfoBean.getDeviceId())
                .params("deviceType",deviceInfoBean.getDeviceType())
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
                .map(new Function<Response<String>, CommonResp>() {
                    @Override
                    public CommonResp apply(Response<String> stringResponse) throws Exception {
                        LogUtils.e("apply","===="+stringResponse.body());
//                        JSON
                        CommonResp resp = JSON.parseObject(stringResponse.body(), CommonResp.class);

                        LogUtils.e("resp",resp.getState()+"=====");
                        return resp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<CommonResp>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        addDisposable(d);
                        LogUtils.e("onSubscribe","   =---====");
                    }

                    @Override
                    public void onNext(CommonResp sensorInfo) {
                        if(sensorInfo!=null){
                            SensorInfoVo object = new Gson().fromJson(sensorInfo.getResult(),SensorInfoVo.class);
                            if(object!=null && object.getSensor().getMsgs()!=null){
                                String msg = object.getSensor().getMsgs().get(0).getMsg();
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(msg);
                                if(jsonObject !=null){
                                    if(jsonObject.has("temperature")){
                                        mTempSensorText.setText(jsonObject.get("temperature").getAsString());
                                    }
                                    if(jsonObject.has("humidity")){
                                        mHumiditySensorText.setText(jsonObject.get("humidity").getAsString());
                                    }
                                }
                            }
                        }
                    }


                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
//                        dismissLoading();
                        LogUtils.e("onComplete","   =====");
                    }
                });
    }
}
