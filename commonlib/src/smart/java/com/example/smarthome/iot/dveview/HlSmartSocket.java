package com.example.smarthome.iot.dveview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smarthome.R;
import com.example.smarthome.iot.DeviceInfoActivity;
import com.example.smarthome.iot.entry.CommonResp;
import com.example.smarthome.iot.entry.DeviceRealStateVo;
import com.example.smarthome.iot.entry.SmartInfoVo;
import com.example.smarthome.iot.entry.eventbus.UpdateFamilyEvent;
import com.example.smarthome.iot.net.Constant;
import com.example.smarthome.iot.util.DeviceControlUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.xhwl.commonlib.base.BaseActivity;
import com.xhwl.commonlib.uiutils.LogUtils;

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

/**
 * author: ydm
 * date: 2020/11/5 9:16
 * description: 海令86智能插座
 * update:
 * version:
 */
public class HlSmartSocket extends BaseActivity implements View.OnClickListener {
    private SmartInfoVo.FamilysBean.DeviceInfoBean gateDevices;
    private TextView mSmartSocket;
    private boolean switchOne=false;
    private TextView mTopTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hl_smart_socket);
        addDestoryActivity(this,"Activity");
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if (intent!=null){
            gateDevices = (SmartInfoVo.FamilysBean.DeviceInfoBean) getIntent().getParcelableExtra("deviceItem");
            LogUtils.e("86智能插座", gateDevices.getDeviceName()+ gateDevices.getDeviceId()+" "+ gateDevices.getDeviceType()+" "+ gateDevices.getSupplierId());
        }

        init(gateDevices.getDeviceId(), gateDevices.getDeviceType(), gateDevices.getSupplierId());

        initview();
    }

    @SuppressLint("SetTextI18n")
    private void initview() {
        mSmartSocket = findViewById(R.id.socket_open);
        mTopTitle = findViewById(R.id.top_title);
        LinearLayout mTopBack = findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mSmartSocket.setOnClickListener(this);
        TextView mTopBt = findViewById(R.id.top_btn);
        mTopBt.setOnClickListener(this);
        if(gateDevices !=null){
            mTopTitle.setText(gateDevices.getDeviceName());
        }
        mTopBt.setText("管理");

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
                        List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans = realStateVo.getRealState().getStateInfos();
                        LogUtils.e("86智能插座", "onNext="+realStateVo+"----"+stateInfosBeans);
                        if(stateInfosBeans != null && stateInfosBeans.size() > 0){
                            initSwitch(stateInfosBeans);
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


    @SuppressLint("ResourceAsColor")
    private void initSwitch(List<DeviceRealStateVo.RealStateBean.StateInfosBean> stateInfosBeans) {
        for (DeviceRealStateVo.RealStateBean.StateInfosBean datas:stateInfosBeans) {
            if (datas.getState()!=null){
                JSONObject jsonObject = JSON.parseObject(datas.getState());
                if(jsonObject.getString("State")!=null){
                    String state=jsonObject.getString("State");
                    if (state.equalsIgnoreCase("0")){
                        mSmartSocket.setText(getString(R.string.socket_colse));
                        int color = getResources().getColor(R.color.gray);
                        mSmartSocket.setTextColor(color);
                        switchOne=false;
                    }else {
                        mSmartSocket.setText(getString(R.string.socket_open));
                        int color = getResources().getColor(R.color.green_water);
                        mSmartSocket.setTextColor(color);
                        switchOne=true;
                    }
                }
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(v.getId() == R.id.top_back){
            finish();
        }else if(id == R.id.socket_open){
            // 开启/关闭 开关   state: 0关，1开，2 toggle
            if (switchOne) {
                switchOne = false;
                mSmartSocket.setText(getString(R.string.socket_colse));
                int color = getResources().getColor(R.color.gray);
                mSmartSocket.setTextColor(color);
                DeviceControlUtil.switchControl(this,gateDevices,1,0);
            } else {
                switchOne = true;
                mSmartSocket.setText(getString(R.string.socket_open));
                int color = getResources().getColor(R.color.green_water);
                mSmartSocket.setTextColor(color);
                DeviceControlUtil.switchControl(this,gateDevices,1,1);
            }
        }else if(id== R.id.top_btn){
            Intent intent = new Intent(this, DeviceInfoActivity.class);
            intent.putExtra("deviceInfoBean",gateDevices);
            startActivity(intent);
        }
    }
}